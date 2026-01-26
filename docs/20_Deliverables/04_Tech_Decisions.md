# Tech Decisions

## 1. 패키지 구조 (Global + Domain)
- **선택:** 루트는 `com.dormmate`로 통일한다. 최상위는 `global`과 `domain`으로 분리하며, `domain`은 기능 단위(fridge, auth, inspection, admin, notifications)로 구성한다. 규모가 큰 도메인(fridge)은 slot/bundle/item 하위 모듈로 세분화한다. 횡단 관심사(설정/보안/에러/공통 응답/유틸)는 `global`에 둔다.
- **근거:** Phase 1~5 범위와 API 그룹을 그대로 반영하면서 패키지 경로를 짧게 유지하고, 도메인 소유권/재사용 경계를 명확히 할 수 있다. 기능이 늘어나도 도메인 단위로 확장 가능하다.
- **대안:** 레이어 중심 구조만 유지(상위에 controller/service/repository)하거나, 하위 모듈 분리 없이 단일 도메인으로 유지.

## 2. 도메인 경계/중복 방지 기준
- **선택:** 큰 틀은 `global + domain`을 유지한다. `domain`은 기능 단위(Phase 기반)로 구성하되, 규모가 큰 도메인은 하위 모듈로 분리한다. 중복이 반복되는 것만 `global`로 승격한다.
- **실무 규칙:**
  - 같은 로직/정책이 2개 이상 도메인에서 반복되면 `global`로 승격한다.
  - “소유 도메인”을 정해 참조만 허용한다. 예: 사용자(User)는 auth가 소유, 다른 도메인은 참조만 한다.
  - 도메인 간 직접 의존은 최소화한다. 필요한 경우 공통 DTO/정책만 공유하고, 내부 로직은 분리한다.
  - 냉장고(fridge)처럼 큰 기능은 slot/bundle/item으로 분해해 책임을 축소한다.
- **근거:** 중복을 방치하면 규칙이 분산되고 복잡도가 상승한다. 명확한 승격 기준과 소유권 규칙이 유지보수 비용을 줄인다.
- **대안:** 초기부터 과도한 공통화를 진행하거나, 모든 로직을 단일 도메인에 유지하는 방식.

## 3. 오류 응답 표준 (ProblemDetail + code)
- **선택:** 모든 API의 오류 응답은 `application/problem+json`으로 통일하고, `ProblemDetail`에 `code`를 포함한다.
- **형식:**
  - `type`: URI (선택)
  - `title`: 요약 메시지
  - `status`: HTTP 상태 코드
  - `detail`: 상세 설명
  - `instance`: 요청 식별 URI (선택)
  - `code`: 오류 코드 (문자열)
  - `errors`: 필드 오류 맵 (선택)
    - 형식: `{ "field": ["message1", "message2"] }`
    - 예시: `{ "name": ["필수 입력입니다."], "expiryDate": ["날짜 형식이 올바르지 않습니다."] }`
- **코드 규칙:**
  - 공통 코드(HTTP 상태 기반):
    - `BAD_REQUEST`, `UNAUTHORIZED`, `FORBIDDEN`, `NOT_FOUND`, `CONFLICT`, `VALIDATION_FAILED`, `RATE_LIMITED`, `SERVER_ERROR`
  - 도메인 코드: 대문자 스네이크 케이스로 정의하고 OpenAPI/프론트 코드 매핑에 동기화한다.
- **근거:** 일관된 에러 계약을 통해 프론트/백엔드 간 메시지 매핑과 디버깅을 단순화한다.
- **대안:** HTTP 상태만으로 분기하거나, 모듈마다 임의의 에러 포맷을 사용하는 방식.
## 4. 폴더 구조 결정 (도메인 중심)
- **선택:** 레이어 중심보다 도메인 중심 구조를 채택한다.
- **근거:**
  - 모듈 확장성: 냉장고 외 확장 모듈 추가에 유리
  - 탐색 속도: 기능 단위로 코드가 모여 탐색/수정이 빠름
  - 경계 명확화: 모듈 간 의존과 결합도 관리가 쉬움
  - 개인 유지보수: 기능 단위 사고 흐름과 일치
- **대안:** controller/service/repository 중심 레이어 구조
- **범위:** 백엔드 패키지 구성 기준
- **예시 구조:**
  - domain/
    - fridge/
      - slot/
      - bundle/
      - item/
    - user/
- **관련 로그 링크:** `docs/10_Workspace/Troubleshooting/폴더구조는 어떻게 할까.md`
