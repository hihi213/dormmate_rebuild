# Tech Decisions

> 합의된 기술 결정과 근거를 기록한다. 구현 완료 여부는 `API_Implementation_Status.md`에서 별도로 관리한다.

## 1. 패키지 구조 (Global + Domain)
- **선택:** 루트는 `com.dormmate`로 통일한다. 최상위는 `global`과 `domain`으로 분리하며 현재 `domain`은 실제 구현 범위인 fridge와 user부터 구성한다. 규모가 큰 fridge는 slot/bundle/item 하위 모듈로 세분화한다. 인증·검사·관리자·알림 패키지는 해당 수직 슬라이스를 시작할 때 추가한다. 횡단 관심사(설정/보안/에러/공통 응답/유틸)는 `global`에 둔다.
- **근거:** Phase 1~5 범위와 API 그룹을 그대로 반영하면서 패키지 경로를 짧게 유지하고, 도메인 소유권/재사용 경계를 명확히 할 수 있다. 기능이 늘어나도 도메인 단위로 확장 가능하다.
- **대안:** 레이어 중심 구조만 유지(상위에 controller/service/repository)하거나, 하위 모듈 분리 없이 단일 도메인으로 유지.

## 2. 도메인 경계/중복 방지 기준
- **선택:** 큰 틀은 `global + domain`을 유지한다. `domain`은 기능 단위(Phase 기반)로 구성하되, 규모가 큰 도메인은 하위 모듈로 분리한다. 중복이 반복되는 것만 `global`로 승격한다.
- **실무 규칙:**
  - 반복 횟수만으로 `global`로 승격하지 않는다. 동일한 의미와 변경 이유를 공유하고 특정 도메인이 소유하기 어려울 때만 공통화한다.
  - “소유 도메인”을 정해 참조만 허용한다. User와 인증 자격증명의 경계는 인증 설계 시 별도로 확정한다.
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

## 5. 점진적 ERD

- **상태:** Confirmed
- **날짜:** 2026-07-26
- **선택:** 전체 도메인 ERD를 먼저 확정하지 않고 현재 수직 슬라이스에 필요한 모델, 생명주기와 DB 제약만 설계한다.
- **근거:** 인증·검사·알림 정책이 미확정인 상태에서 전체 관계를 고정하면 재설계 비용이 커진다. 작은 범위의 계약·정책·테스트와 함께 모델을 확장하면 결정 근거를 추적할 수 있다.
- **적용:** `GET /fridge/slots`에서는 User 참조, Slot 소속과 층 조회에 필요한 최소 Fridge, FridgeSlot, FridgeSlotAssignment와 occupiedCount 계산에 필요한 FridgeBundle 최소 범위만 다룬다.
- **대안:** 전체 ERD 선행 설계.

## 6. Slot 목록 입력 오류 처리

- **상태:** Confirmed
- **날짜:** 2026-07-26
- **관련 정책:** `INV-010`
- **선택:** 잘못된 `view`, 음수 `page`, 범위를 벗어난 `size`를 fallback 또는 clamp하지 않고 `400 ProblemDetail`로 반환한다.
- **근거:** 호출자 오류를 조용히 숨기지 않고 계약 위반을 조기에 발견하기 위해서다. `size=200`은 Slot 수가 제한적인 현재 UI 호환 범위로만 허용하며 다른 목록 API의 공통 기준으로 일반화하지 않는다.
- **대안:** 알 수 없는 값을 기본값으로 보정.

## 7. Redis는 Post-MVP 비교 후보

- **상태:** Confirmed
- **날짜:** 2026-07-26
- **선택:** Redis를 현재 아키텍처나 MVP 필수 의존성으로 두지 않는다. 검사 동시성은 DB 제약, 트랜잭션과 JPA 락을 먼저 검증한 뒤 Redis Lock과 비교한다.
- **근거:** 문제와 측정 없이 분산 락을 도입하지 않으면서 Redis 사용 경험과 기술 선택 근거를 함께 확보하기 위해서다.
- **조건:** Redis Session은 인증 방식과 배포 구조가 확정된 뒤 별도로 판단한다.

## 8. 프론트엔드 변경 경계

- **상태:** Confirmed
- **날짜:** 2026-07-26
- **관련 정책:** `PRJ-001`~`PRJ-004`
- **선택:** 기존 UI·UX는 유지한다. API Client, 타입, 인증 전달과 매핑은 사용자가 요청한 연동 작업에서만 수정한다.
- **근거:** 백엔드 학습 범위를 유지하면서 기존 화면을 실제 계약 검증 수단으로 활용하기 위해서다.
- **후속 처리:** 확인된 프론트 기술 부채는 `Frontend_Integration_Baseline.md`에 기록하고 관련 API 연동 시 처리한다.

## 9. 임시 스키마 관리 전략

- **상태:** Temporary
- **날짜:** 2026-07-26
- **선택:** 초기 모델 탐색 단계에서는 `ddl-auto=update`를 사용한다.
- **근거:** 현재 스켈레톤 단계에서 작은 수직 슬라이스별로 모델을 조정하고 있기 때문이다.
- **제약:** 운영 배포나 재현 가능한 스키마 관리 전략으로 사용하지 않는다.
- **전환 조건:** 핵심 MVP 스키마가 안정되면 Flyway 초기 마이그레이션을 작성하고 `ddl-auto=validate`로 전환한다.
- **검증:** 테스트 DB에서도 같은 마이그레이션으로 스키마를 재현한다.

## 10. MVP의 수동 DTO 매핑

- **상태:** Confirmed
- **날짜:** 2026-07-26
- **선택:** MVP에서는 MapStruct 없이 Entity와 DTO를 명시적으로 수동 매핑한다.
- **근거:** Entity와 API 계약의 차이, 계산 필드, 권한별 응답 필드를 사용자가 직접 이해하고 드러내기 위해서다.
- **주의:** 수동 매핑 자체가 민감 정보 노출을 자동으로 방지하지 않으므로 응답 DTO와 테스트로 별도 검증한다.
- **재검토 조건:** 반복 매핑 코드가 실제 유지보수 문제로 확인될 때 자동 매핑 도구를 비교한다.

## 11. Docker Compose 기반 로컬 재현

- **상태:** Confirmed
- **날짜:** 2026-07-26
- **선택:** 로컬 통합 실행과 PostgreSQL 환경 재현에 Docker Compose를 사용한다.
- **근거:** 서비스 연결, PostgreSQL 버전과 환경 변수를 저장소에서 재현하고 개발 장비 차이를 줄일 수 있다.
- **범위:** 로컬·통합 검증 환경에 대한 결정이며 클라우드 또는 운영 배포 플랫폼 선택과는 별개다.

## 12. Slot MVP 모델과 확장 조건

- **상태:** Confirmed
- **날짜:** 2026-07-26
- **관련 정책:** `INV-012`, `INV-019`~`INV-023`
- **현재 선택:** Slot은 `slotId`로 식별하고 `fridgeId`로 물리적 냉장고에 소속시킨다. 사용자는 관리자가 지정한 필수 `displayName`으로 칸을 구분한다. 현재 표시 순서 요구가 없으므로 `slotIndex`, `slotLetter`, `position`, `displayOrder`는 도입하지 않는다.
- **기존 의도:** 기존의 `floorNo + slotIndex` 구조는 층 안에서 Slot을 바로 구분하고 Fridge 조회나 조인을 줄이려는 의도로 도입됐던 것으로 검토했다. `slotLetter`는 `slotIndex`를 `A`, `B`, `C`로 바꾼 사용자 표시값이었다.
- **대안 검토:** Slot에 `floorNo`와 층 단위 위치값을 직접 저장하고 물리적 Fridge 관계를 생략하는 방식을 검토했다.
- **대안을 선택하지 않은 이유:** Fridge는 관리자 추가·삭제와 물리적 설비 관리에 필요한 개념이다. 현재 규모에서 FK/PK 기반 Fridge 조인이 병목이라는 근거가 없으며, 측정 없이 조회를 줄이기 위해 관계를 제거하면 Slot의 층 정보와 냉장고 소속을 별도로 동기화해야 한다.
- **MVP 근거:** 영속 식별, 냉장고 소속과 사용자 구분이라는 현재 요구는 `slotId`, `fridgeId`, `displayName`으로 충족된다. UUID인 `slotId`는 API 관계와 영속 식별에는 적합하지만 사용자가 기억하거나 실물 냉장고와 대조하기에는 부적합하므로 사용자 표시는 `displayName`이 담당한다. 순서나 알파벳 계산 필드를 미리 추가하면 역할이 겹치고 변경 규칙만 늘어난다.
- **조회 선택:** 거주자에게 현재 배정된 Slot은 `RETIRED`를 포함해 상태와 함께 반환한다. 퇴역 사실 때문에 기존 배정과 포장이 사라진 것처럼 보이지 않게 하기 위해서다. 다만 퇴역 Slot은 신규 포장, 신규 배정과 일반 변경에 사용할 수 없다.
- **현재 정렬:** `floorNo → fridgeId → displayName → slotId`를 사용한다. `fridgeId` 순서는 업무상 표시 순서가 아니라 냉장고별 그룹과 페이지 결과를 안정적으로 유지하기 위한 기술적 순서다.
- **현재 단순화:** Fridge는 Slot 조회에 필요한 식별자와 설치 층부터 설계한다. 냉장고 표시명·운영 상태·삭제 생명주기와 Slot 표시 순서 관리는 해당 관리자 기능에서 확장한다.
- **재검토 조건:** 실제 쿼리와 실행 계획에서 Fridge 조인이 병목으로 확인되면 인덱스를 먼저 검토하고, 부족할 때만 `floorNo` 비정규화를 비교한다.
- **재검토 조건:** 관리자가 냉장고나 칸의 표시 순서를 직접 관리해야 하거나, 물리적 위치 기반 정렬이 사용자 흐름에 필요하다고 확인되면 `displayOrder` 또는 별도 위치 모델을 비교한다.
- **재검토 조건:** Slot 퇴역 후 이름 재사용, 복구, 감사 이력 요구가 확정되면 `displayName` 유일성 범위와 당시 표시명 스냅샷을 다시 설계한다.

## 13. 포장 영속 식별자와 사용자 라벨 분리

- **상태:** Partial Confirmed
- **날짜:** 2026-07-26
- **관련 정책:** `INV-013`, `INV-014`, `INV-016`~`INV-018`
- **현재 선택:** 포장의 영속 식별에는 UUID `bundleId`를 사용한다. 사용자용 `labelNumber`는 동일 Slot 안의 `1..999` 정수로 저장하고 화면에서 3자리로 표시한다. 관계와 유일성 검토의 원천은 `slotId`와 `labelNumber`이며, `"001"`이나 조합 문자열을 영속 식별자로 사용하지 않는다.
- **근거:** 앞자리 0은 데이터 의미가 아니라 표시 형식이다. 숫자로 저장하면 범위 검증과 비교가 단순하고 `"1"`과 `"001"`이 서로 다른 값으로 저장되는 문제를 피할 수 있다. 표시 형식 변경도 저장 구조에 영향을 주지 않는다.
- **용어 선택:** `포장 번호`는 `bundleId`와 `labelNumber`를 혼동시키므로 공식 용어로 사용하지 않는다.
- **아직 확정하지 않음:** 번호 발급 알고리즘, 동시 생성 제약, `999` 소진 처리, 소프트 삭제 후 재사용, 복원 충돌, `slotLabel`·`labelDisplay`의 최종 계약은 Bundle 생성 수직 슬라이스에서 확정한다.

## 14. 인증 전달 방식과 도메인 권한 입력 분리

- **상태:** Partial Confirmed
- **날짜:** 2026-07-26
- **관련 정책:** `AUTH-001`, `AUTH-004`
- **현재 선택:** Slot Service는 HTTP 세션이나 토큰 객체를 직접 받지 않고 현재 사용자의 `userId`, 역할과 담당 층을 표현하는 내부 인증 주체(`CurrentActor` 후보)를 입력으로 받는다.
- **근거:** 역할별 Slot 조회 규칙을 미확정 인증 전달 방식과 분리해 단위 테스트할 수 있고, 이후 세션 또는 토큰을 선택해도 Service 업무 규칙을 바꾸지 않기 위해서다.
- **MVP 경계:** `CurrentActor`는 인증 우회용 fixture가 아니다. Controller에서 실제 인증 주체로 변환하는 작업과 `401`, `403` 통합 검증은 `AUTH-001`과 보안 계약을 확정한 뒤 완료한다.
- **아직 확정하지 않음:** 세션 또는 Access/Refresh Token 선택, 쿠키·헤더 계약, CSRF, 로그아웃과 다중 기기 정책.
