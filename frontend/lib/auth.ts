import { safeApiCall } from "@/lib/api-client"

export type UserRole = "RESIDENT" | "FLOOR_MANAGER" | "ADMIN"

export type RoomDetails = {
  roomId?: string
  floorNo?: number
  roomNumber?: string
  personalNo?: number
  floorCode?: string
}

export type AuthUser = {
  userId: string
  loginId: string
  name: string
  room?: string
  roomDetails?: RoomDetails | null
  roles: UserRole[]
  isFloorManager: boolean
  isAdmin: boolean
}

type RoomAssignment = {
  roomId: string
  floorNo: number
  roomNumber: string
  personalNo: number
  assignedAt: string
  floorCode?: string
}

type UserProfile = {
  userId: string
  loginId: string
  displayName: string
  email?: string | null
  accountAuthorities: string[]
  primaryRoom?: RoomAssignment | null
  isResident: boolean
  isFridgeManager: boolean
  isAdmin: boolean
  createdAt: string
  updatedAt: string
}

type LoginResponse = UserProfile

type CsrfTokenResponse = {
  headerName: string
  parameterName: string
  token: string
}

const PROFILE_KEY = "dm.auth.profile"
const ADMIN_FLAG_COOKIE = "dm.admin"

const authListeners = new Set<(user: AuthUser | null) => void>()

let csrfToken: CsrfTokenResponse | null = null
let csrfTokenPromise: Promise<CsrfTokenResponse> | null = null

export async function prepareLoginCsrfToken(): Promise<void> {
  if (csrfToken) return
  if (!csrfTokenPromise) {
    csrfTokenPromise = safeApiCall<CsrfTokenResponse>("/csrf", {
      method: "GET",
      skipAuth: true,
    }).then(({ data, error }) => {
      if (error || !data) {
        throw new Error(error?.message ?? "로그인 보안 정보를 준비하지 못했습니다.")
      }
      csrfToken = data
      return data
    }).finally(() => {
      csrfTokenPromise = null
    })
  }
  await csrfTokenPromise
}

function mapUserProfile(profile: UserProfile): AuthUser {
  const roomDetails = profile.primaryRoom
    ? {
        roomId: profile.primaryRoom.roomId,
        floorNo: profile.primaryRoom.floorNo,
        roomNumber: profile.primaryRoom.roomNumber,
        personalNo: profile.primaryRoom.personalNo,
        floorCode: profile.primaryRoom.floorCode,
      }
    : null
  return {
    userId: profile.userId,
    loginId: profile.loginId,
    name: profile.displayName,
    room: formatRoom(profile.primaryRoom ?? undefined),
    roomDetails,
    roles: [
      ...(profile.isResident ? (["RESIDENT"] as UserRole[]) : []),
      ...(profile.isFridgeManager ? (["FLOOR_MANAGER"] as UserRole[]) : []),
      ...(profile.isAdmin ? (["ADMIN"] as UserRole[]) : []),
    ],
    isFloorManager: profile.isFridgeManager,
    isAdmin: profile.isAdmin,
  }
}

function formatRoom(room?: RoomAssignment | null): string | undefined {
  if (!room) return undefined
  const base = room.roomNumber ? `${room.roomNumber}호` : ""
  const personal = room.personalNo ? ` ${room.personalNo}번` : ""
  return `${room.floorNo}층 ${base}${personal}`.trim()
}

function readUser(): AuthUser | null {
  if (typeof window === "undefined") return null
  try {
    const raw = localStorage.getItem(PROFILE_KEY)
    if (!raw) return null
    return JSON.parse(raw) as AuthUser
  } catch {
    return null
  }
}

function writeUser(user: AuthUser | null) {
  if (typeof window === "undefined") return
  if (!user) {
    localStorage.removeItem(PROFILE_KEY)
    clearAdminCookie()
    return
  }
  localStorage.setItem(PROFILE_KEY, JSON.stringify(user))
  syncAdminCookie(user)
}

function clearAdminCookie() {
  if (typeof document === "undefined") return
  document.cookie = `${ADMIN_FLAG_COOKIE}=; Max-Age=0; Path=/; SameSite=Lax`
}

function syncAdminCookie(user: AuthUser | null) {
  if (typeof document === "undefined") return
  if (user?.isAdmin) {
    document.cookie = `${ADMIN_FLAG_COOKIE}=1; Max-Age=604800; Path=/; SameSite=Lax`
  } else {
    clearAdminCookie()
  }
}

function notifyAuth(user: AuthUser | null) {
  authListeners.forEach((listener) => {
    try {
      listener(user)
    } catch (error) {
      console.error("auth listener error", error)
    }
  })
}

function applySession(response: LoginResponse) {
  const user = mapUserProfile(response)
  writeUser(user)
  notifyAuth(user)
}

function clearSession() {
  writeUser(null)
  notifyAuth(null)
}

export function subscribeAuth(listener: (user: AuthUser | null) => void) {
  authListeners.add(listener)
  return () => {
    authListeners.delete(listener)
  }
}

export function getCurrentUser(): AuthUser | null {
  return readUser()
}

export function getCurrentUserId(): string | null {
  return readUser()?.userId ?? null
}

export function getCurrentUserLoginId(): string | null {
  return readUser()?.loginId ?? null
}

export async function loginWithCredentials({ id, password }: { id: string; password: string }) {
  if (!csrfToken) {
    throw new Error("로그인 보안 정보가 준비되지 않았습니다. 잠시 후 다시 시도해 주세요.")
  }
  const loginCsrfToken = csrfToken
  const { data, error } = await safeApiCall<LoginResponse>("/auth/login", {
    method: "POST",
    body: { loginId: id, password },
    headers: {
      [loginCsrfToken.headerName]: loginCsrfToken.token,
    },
    skipAuth: true,
  })

  if (error || !data) {
    if (error?.code === "CSRF_INVALID") {
      csrfToken = null
    }
    const loginError = new Error(
      error?.message ?? "로그인에 실패했습니다. 다시 시도해 주세요.",
    ) as Error & { code?: string }
    loginError.code = error?.code
    throw loginError
  }

  applySession(data)
  csrfToken = null
  return getCurrentUser()
}

export async function logout() {
  await prepareLoginCsrfToken()
  if (!csrfToken) {
    throw new Error("로그아웃 보안 정보를 준비하지 못했습니다.")
  }
  const logoutCsrfToken = csrfToken
  const { error } = await safeApiCall("/auth/logout", {
    method: "POST",
    headers: {
      [logoutCsrfToken.headerName]: logoutCsrfToken.token,
    },
    parseResponseAs: "none",
  })
  if (error?.code === "CSRF_INVALID") {
    csrfToken = null
  }
  if (error) {
    throw new Error(error.message)
  }
  csrfToken = null
  clearSession()
}

export async function fetchProfile(): Promise<AuthUser | null> {
  const { data, error } = await safeApiCall<UserProfile>("/profile/me", {
    method: "GET",
  })

  if (error || !data) {
    if (error?.status === 401) {
      clearSession()
    }
    return null
  }

  const user = mapUserProfile(data)
  writeUser(user)
  notifyAuth(user)
  return user
}

type RedirectToLoginOptions = {
  redirect?: string
  reason?: string
  navigate?: boolean
  preserveSession?: boolean
}

export function redirectToLogin(reason?: string): string
export function redirectToLogin(options?: RedirectToLoginOptions): string
export function redirectToLogin(
  arg0?: string | RedirectToLoginOptions,
  maybeOptions?: RedirectToLoginOptions,
): string {
  const options = typeof arg0 === "string" ? { ...(maybeOptions ?? {}), reason: arg0 } : arg0 ?? {}
  const { redirect, reason, navigate = false, preserveSession = false } = options
  if (
    process.env.NEXT_PUBLIC_FIXTURE === "1" ||
    (typeof window !== "undefined" && window.localStorage.getItem("dm.fixture") === "1")
  ) {
    return "/"
  }
  if (!preserveSession) {
    clearSession()
  }
  const params = new URLSearchParams()
  params.set("mode", "login")
  if (redirect) {
    params.set("redirect", redirect)
  }
  if (reason) {
    params.set("reason", reason)
  }
  const loginUrl = `/auth?${params.toString()}`
  if (navigate && typeof window !== "undefined") {
    window.location.href = loginUrl
  }
  return loginUrl
}

export async function registerUser(): Promise<AuthUser> {
  throw new Error("회원가입은 현재 관리자 승인 절차를 통해서만 가능합니다.")
}
