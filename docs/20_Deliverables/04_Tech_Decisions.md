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
## 4. 폴더 구조 결정 (도메인 중심, Flat)
- **선택:** 도메인 중심 구조를 유지하되, 각 도메인 하위는 Flat으로 둔다.
- **근거:**
  - 모듈 확장성: 냉장고 외 확장 모듈 추가에 유리
  - 탐색 속도: 깊이를 줄여 파일 탐색 비용 감소
  - 경계 명확화: 도메인별 책임이 분리되면서도 과도한 계층 분리를 피함
  - 학습 효율: 초보 개발자가 구조를 빠르게 파악 가능
- **대안:** 도메인 내부에 controller/service/repository/entity/dto 폴더를 중첩하는 레이어 구조
- **범위:** 백엔드 패키지 구성 기준
- **예시 구조:**
  - domain/
    - fridge/
      - slot/
        - FridgeSlot.java
        - FridgeSlotRepository.java
        - FridgeSlotService.java
        - FridgeSlotController.java
        - FridgeSlotRequest.java
        - FridgeSlotResponse.java
      - bundle/
        - FridgeBundle.java
        - FridgeBundleRepository.java
        - FridgeBundleService.java
        - FridgeBundleController.java
        - FridgeBundleRequest.java
        - FridgeBundleResponse.java
      - item/
        - FridgeItem.java
        - FridgeItemRepository.java
        - FridgeItemService.java
        - FridgeItemController.java
        - FridgeItemRequest.java
        - FridgeItemResponse.java
    - user/
      - User.java
      - UserRepository.java
      - UserService.java
      - UserController.java
      - UserRequest.java
      - UserResponse.java
- **관련 로그 링크:** `docs/10_Workspace/Troubleshooting/폴더구조는 어떻게 할까.md`
