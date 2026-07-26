# API Implementation Status

> Release 1 MVP와 현재 작업 API의 구현·검증 상태를 가볍게 관리한다. OpenAPI 전체를 복제하지 않는다.

- API 계약: `docs/20_Deliverables/03_API_Specification.md`
- 정책: `docs/20_Deliverables/06_Policy_Baseline.md`
- 상세 설계와 작업 기록: 관련 Phase·Task

## 상태 정의

### 범위

- `MVP`: Release 1 대상
- `Conditional MVP`: 선행 결정에 따라 MVP 포함 여부 결정
- `Post-MVP`: Release 1 이후

### 진행

- `Review Required`: 정책 또는 계약 결정 전
- `Not Started`: 확정됐지만 구현 전
- `In Progress`: 현재 작업 중
- `Implemented`: 구현과 검증 완료

## Release 1

### 물품 관리

| Method | Path | Scope | Status | Policy / Blocker | Task |
| --- | --- | --- | --- | --- | --- |
| GET | `/fridge/slots` | MVP | In Progress | `INV-001`, `INV-009`~`INV-012`, `INV-019`~`INV-021`, `INV-023`, `INV-024`, `AUTH-001`, `AUTH-006`~`AUTH-009`; Slot 영속성 세부 제약과 응답 enum 확정 필요, `INV-022`는 구현 비차단 | [Slot 조회 및 검증](./Tasks/Task_Slot%20조회%20및%20검증.md) |
| GET | `/fridge/bundles` | MVP | Review Required | `INV-007`: 검색·필터·페이징 범위 | - |
| POST | `/fridge/bundles` | MVP | Review Required | `INV-001`~`INV-006`, `INV-013`, `INV-014` | - |
| GET | `/fridge/bundles/{bundleId}` | MVP | Not Started | `INV-001`~`INV-005` | - |
| PATCH | `/fridge/bundles/{bundleId}` | MVP | Not Started | `INV-002`~`INV-005` | - |
| DELETE | `/fridge/bundles/{bundleId}` | MVP | Review Required | `INV-005`, `INV-008` | - |
| POST | `/fridge/bundles/{bundleId}/items` | MVP | Not Started | `INV-002`, `INV-004` | - |
| PATCH | `/fridge/items/{itemId}` | MVP | Not Started | `INV-002`, `INV-004` | - |
| DELETE | `/fridge/items/{itemId}` | MVP | Review Required | `INV-005`, `INV-008` | - |

### 인증과 사용자

| Method | Path | Scope | Status | Policy / Blocker |
| --- | --- | --- | --- | --- |
| POST | `/auth/login` | MVP | Review Required | 세션 생성 성공 응답과 실패 오류 계약 확정 필요, `AUTH-001`, `AUTH-003`, `AUTH-008` |
| POST | `/auth/logout` | MVP | Review Required | 세션 무효화 응답과 CSRF 계약 확정 필요, `AUTH-001`, `AUTH-005`, `AUTH-008` |
| GET | `/csrf` | MVP | Not Started | `AUTH-008`; 로그인·로그아웃과 상태 변경 요청 전에 토큰 발급 |
| GET | `/profile/me` | MVP | Review Required | 응답 필수 필드 확정 필요 |

온라인 회원가입 API는 현재 OpenAPI에 없으며 `AUTH-002` 결정 전 추가하지 않는다.
기존 `/auth/refresh`는 세션 인증 MVP에서 사용하지 않는다. 기존 OpenAPI 계약의
제거 또는 Legacy 표시는 인증 API 계약 Task에서 처리한다.

### 검사

| Method | Path | Scope | Status | Policy / Blocker |
| --- | --- | --- | --- | --- |
| POST | `/fridge/inspections` | MVP | Review Required | `INSP-001`, `INSP-004`, `INSP-005` |
| GET | `/fridge/inspections/{sessionId}` | MVP | Review Required | 조회 권한과 세션 상태 계약 |
| POST | `/fridge/inspections/{sessionId}/actions` | MVP | Review Required | `INSP-001`, 조치 입력 계약 |
| POST | `/fridge/inspections/{sessionId}/submit` | MVP | Review Required | `INSP-002`~`INSP-005` |
| GET | `/fridge/inspections` | MVP | Review Required | 거주자 이력·관리자 조회 범위 |

일정 관리, 정정, 알림 재발송과 SSE는 MVP 구현 현황에 포함하지 않는다.

### 최소 관리자

| Method | Path | Scope | Status | Policy / Blocker |
| --- | --- | --- | --- | --- |
| GET | `/admin/users` | MVP | Review Required | `ADM-001`, `ADM-002` |
| GET | `/admin/fridge/issues` | MVP | Review Required | 물품 조회 범위와 응답 계약 |
| PATCH | `/admin/users/{userId}/status` | Conditional MVP | Review Required | 상태 변경 범위와 감사 정책 |

현재 OpenAPI에 최소 관리자 검사 이력 전용 API가 명확하지 않다. 기존 `/fridge/inspections`로 해결할지 계약 변경이 필요한지 Phase 4에서 결정한다.

기존 층별장 역할 승격·해제 API는 Slot 단위 관리 배정 정책과 충돌해 OpenAPI에서
제거했다. 냉장고 담당자의 Slot 배정·해제 API는 관리자 수직 슬라이스에서 요청
형식, 일괄 배정, 활성 중복과 변경 사유 계약을 확정한 뒤 추가한다.

## Post-MVP 요약

- 알림 저장·조회·읽음 처리와 환경설정
- 검사 일정 고급 관리
- 제출 결과 정정과 벌점 재계산
- SSE 다중 참여
- Redis Lock 비교
- 감사 로그와 운영 자동화

## Implemented 판정 기준

다음 조건을 모두 충족해야 `Implemented`로 변경한다.

- 관련 테스트 통과
- OpenAPI 계약 일치
- 권한과 주요 실패 흐름 검증
- 필요한 DB 제약과 동시성 위험 검토
- 사용자가 요청한 경우 프론트 연동 완료
- 관련 Phase·Task·정책·결정 문서 갱신
