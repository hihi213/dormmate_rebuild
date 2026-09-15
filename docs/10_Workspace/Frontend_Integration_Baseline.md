# Frontend Integration Baseline

> 기존 UI·UX를 유지하면서 백엔드 계약과 연동할 때 확인할 현재 프론트 동작을 기록한다. 이 문서는 정책 SSOT가 아니며, 필수 연동과 UI 변경의 권한 경계는 `AGENTS.md`를 따른다.

## 1. 유지 경계

- 기존 화면 구조, 디자인과 사용자 흐름을 유지한다.
- API Client, 타입, 인증 전달과 요청·응답 매핑은 변경 가능 영역이다.
- UI·UX 변경이 필요하면 먼저 사용자와 합의한다.
- 프론트 어댑터로 백엔드 계약 오류를 숨기지 않는다.

## 2. 확인된 현재 동작

### 인증

아래 세 항목은 전환 전 원본 동작이며 현재 구현 상태가 아니다.

- Bearer access token과 refresh token을 사용한다.
- token과 `deviceId`를 `localStorage`에 보관한다.
- `/auth/refresh` 호출과 주기적인 인증 상태 확인이 있다.

현재 유지되는 화면 동작:

- 회원가입 화면은 안내 상태이며 실제 가입 요청은 비활성화돼 있다.
- fixture 모드에서는 인증 Guard를 우회할 수 있다.

인증 방식 전환: Rebuild MVP는 `AUTH-001`에서 서버 세션만
사용하고 토큰 인증은 `AUTH-011`의 Post-MVP 후보로 분리했다. `AUTH-002`와
`AUTH-005`의 미결 세부 정책을 자동으로 확정하지 않는다.
모든 API 요청에는 세션 쿠키 전달을 위해 `credentials: include`를 기본 적용했다.
로그인 요청은 `AUTH-012`에 따라 `deviceId` 없이 자격 증명만 보내고 응답
`UserProfileResponse`로 화면 상태를 구성한다. Bearer 헤더, Access/Refresh
Token 저장·갱신과 `deviceId` 인증 코드는 제거했다. 로그아웃은
`AUTH-017`에 따라 CSRF 헤더와 세션 쿠키만 전달하고 요청 본문 없이 호출한다.

현재 물품 등 일반 변경 요청의 공통 CSRF 전달과 실제 Spring Security 연동은
아직 검증되지 않았다. 로그인·로그아웃 코드 전환을 전체 인증 연동 완료로 보지 않는다.

### 물품

- Slot은 `page=0&size=200` 형태로 호출한다.
- 거주자 Slot 조회 DTO와 화면 표시는 `slotId`, `fridgeId`, `displayName`으로 전환했다.
- Bundle·검사·관리자 매핑에는 `slotIndex`, `slotLabel` 등 과거 표시 전제가 남아 있다.
- 현재 `A001` 표시는 `slotIndex`에서 계산한 알파벳과 `labelNumber`를
  조합한다.
- Bundle 목록은 큰 페이지를 받은 뒤 검색·필터·통계 일부를 클라이언트에서 수행한다.
- 포장 생성 UI는 물품을 최소 1개 요구한다.
- 메모는 소유자가 아닌 사용자에게 숨긴다.

### 검사

- 검사 개요는 약 6초, 진행 세션은 약 30초 간격으로 상태를 다시 조회한다.
- 검사 입력 초안은 로컬에 최대 2일 보존한다.
- 모든 대상을 처리해야 제출할 수 있다.

폴링 주기와 로컬 초안 기간은 프론트 구현 세부사항이며 별도 합의 없이 백엔드 정책으로 사용하지 않는다.

### 관리자와 알림

- 일부 관리자 화면은 API 실패 시 mock 또는 기본값으로 대체한다.
- Service Worker에는 알림 표시 코드가 있지만 Push 구독 등록·서버 발송 흐름은 확인되지 않았다.

mock 또는 fallback 화면은 API 구현 완료의 증거가 아니다.

## 3. 연동 시 처리할 기술 부채

다음 항목은 확정 계약이 없거나 서로 충돌하여 관련 API 검토 후 처리한다.

- `ErrorPayload`의 `errors` 중복 선언
- Next 설정에서 TypeScript 또는 ESLint 오류를 무시하는 옵션
- 관리자 mock·fallback과 실제 API 실패 상태 구분
- Web Push 구독·해제 API 부재
- 거주자 Slot 조회 DTO와 화면 표시는 `slotId`, `fridgeId`, `displayName`으로
  전환했다. Bundle·Inspection 화면에는 아직 각 계약 자체에 남아 있는
  `slotIndex`, `slotLabel` 기반 표시가 존재한다.
- Bundle·Inspection·Reallocation·Issue 연동 시 남아 있는 `slotLabel`,
  `slotIndex`, `compartmentId`, `fridgeCompartmentId`가 `slotId`와 같은
  개념인지 확인하고 확정 계약에 맞춰 타입과 매핑을 통일
- `A001` 계산은 `INV-017`의 `labelDisplay` 계약을 확정한 뒤 제거하거나
  `displayName + labelNumber` 기반 표현으로 변경
- 기존 `FLOOR_MANAGER`, `isFloorManager`, `floorManagerOnly`와 역할
  승격·해제 호출을 제거하고, `isFridgeManager`, `fridgeManagerOnly`와
  Slot 관리 배정 흐름으로 교체. 현재 OpenAPI에는 관리자 배정 생성·해제
  엔드포인트가 없어 UI를 완결할 수 없다.
- 관리자는 거주자 권한을 상속하지 않으므로 일반 물품 등록 UI를 관리자에게
  권한 상속 방식으로 노출하지 않음

### 타입 오류 처리 원칙

- 백엔드 재개발로 OpenAPI 타입이 바뀌었지만 기존 화면과 어댑터가 과거 필드를
  참조해 발생한 오류는, 과거 필드를 프론트 타입에 임의로 복원해 숨기지 않는다.
- 중복 선언, Next.js 페이지 규약과 컴포넌트 타입처럼 백엔드 계약과 무관한
  명백한 오류는 프론트 타입 기준선 정비 범위에서 처리할 수 있다.
- 계약에 의존하는 오류는 해당 백엔드 수직 슬라이스에서 정책과 OpenAPI를 확정한
  뒤 API Client, 타입, 인증 전달과 요청·응답 매핑을 함께 수정한다.
- Post-MVP 화면의 미확정 계약에서 비롯된 오류는 현재 릴리스 계약으로 억지로
  보정하지 않고 이 문서의 연동 부채로 유지한다.
- Release 1 계약 연동을 완료할 때 전체 `tsc --noEmit`, lint와 build 통과를
  목표로 하며, 그전까지 타입 검사 생략 build를 완전한 검증으로 간주하지 않는다.

## 4. 연동 완료 기준

확정 계약에 따른 필수 연동 또는 사용자가 요청한 연동에서 다음을 확인한다.

- 요청 Method·Path·Body가 OpenAPI와 일치
- 응답 타입이 실제 schema 이름과 일치
- 인증 정보 전달과 401 재처리 동작
- ProblemDetail 오류 매핑
- fixture를 끈 상태의 실제 API 흐름
- TypeScript 검사, lint와 build
- 관련 주요 사용자 흐름

TypeScript나 ESLint 오류를 무시한 build 성공은 완전한 검증으로 간주하지 않는다.
