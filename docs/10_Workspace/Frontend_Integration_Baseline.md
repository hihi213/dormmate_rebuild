# Frontend Integration Baseline

> 기존 UI·UX를 유지하면서 백엔드 계약과 연동할 때 확인할 현재 프론트 동작을 기록한다. 이 문서는 정책 SSOT가 아니며, 프론트 수정은 사용자 요청이 있을 때만 수행한다.

## 1. 유지 경계

- 기존 화면 구조, 디자인과 사용자 흐름을 유지한다.
- API Client, 타입, 인증 전달과 요청·응답 매핑은 변경 가능 영역이다.
- UI·UX 변경이 필요하면 먼저 사용자와 합의한다.
- 프론트 어댑터로 백엔드 계약 오류를 숨기지 않는다.

## 2. 확인된 현재 동작

### 인증

- Bearer access token과 refresh token을 사용한다.
- token과 `deviceId`를 `localStorage`에 보관한다.
- `/auth/refresh` 호출과 주기적인 인증 상태 확인이 있다.
- 회원가입 화면은 안내 상태이며 실제 가입 요청은 비활성화돼 있다.
- fixture 모드에서는 인증 Guard를 우회할 수 있다.

이 동작은 원본 프론트의 관찰 결과다. Rebuild는 `AUTH-001`에서 서버 세션을
선택했으며, `AUTH-002`와 `AUTH-005`의 미결 세부 정책을 자동으로 확정하지 않는다.

### 물품

- Slot은 `view=full&page=0&size=200` 형태로 호출한다.
- 현재 Slot·Bundle·검사·관리자 매핑은 `slotIndex`, `slotLetter`,
  `slotLabel`과 `toSlotLetter()` 기반 fallback을 사용한다.
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

다음 항목은 지금 수정하지 않고 관련 API 연동 요청이 있을 때 처리한다.

- generated OpenAPI schema 이름과 프론트 참조 타입 이름 불일치
- `ErrorPayload`의 `errors` 중복 선언
- Next 설정에서 TypeScript 또는 ESLint 오류를 무시하는 옵션
- 관리자 mock·fallback과 실제 API 실패 상태 구분
- Web Push 구독·해제 API 부재
- Slot 계약 연동 시 API DTO의 `slotIndex`, `slotLetter`를 제거하고 관계와
  선택에는 `slotId`, 소속에는 `fridgeId`, 사용자 표기에는 `displayName`을
  사용
- Slot 계약 연동 시 `toSlotLetter()` fallback과 `slotIndex` 기반 칸 표시를
  제거
- Bundle·Inspection·Reallocation·Issue 연동 시 남아 있는 `slotLabel`,
  `slotIndex`, `compartmentId`, `fridgeCompartmentId`가 `slotId`와 같은
  개념인지 확인하고 확정 계약에 맞춰 타입과 매핑을 통일
- `A001` 계산은 `INV-017`의 `labelDisplay` 계약을 확정한 뒤 제거하거나
  `displayName + labelNumber` 기반 표현으로 변경

## 4. 연동 완료 기준

사용자가 프론트 연동을 요청한 경우 다음을 확인한다.

- 요청 Method·Path·Body가 OpenAPI와 일치
- 응답 타입이 실제 schema 이름과 일치
- 인증 정보 전달과 401 재처리 동작
- ProblemDetail 오류 매핑
- fixture를 끈 상태의 실제 API 흐름
- TypeScript 검사, lint와 build
- 관련 주요 사용자 흐름

TypeScript나 ESLint 오류를 무시한 build 성공은 완전한 검증으로 간주하지 않는다.
