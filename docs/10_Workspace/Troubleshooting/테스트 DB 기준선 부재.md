# 테스트 DB 기준선 부재

> Status: Open
> 확인일: 2026-07-26

## 현상

백엔드 스켈레톤에서 다음 명령을 실행하면 두 테스트가 실패한다.

```text
cd backend
./gradlew test
```

확인된 실패:

- `BackendApplicationTests.contextLoads()`
- `ProblemDetailValidationTest.validationErrorsContainFieldMessages()`

공통 원인은 테스트 ApplicationContext가 사용할 DataSource에 연결하지 못해
Hibernate가 Dialect를 결정하지 못하는 것이다.

## 현재 구성

- 기본 설정은 로컬 PostgreSQL `jdbc:postgresql://localhost:5432/dormmate_rebuild_db`를 사용한다.
- 테스트 전용 설정이나 테스트용 DB 의존성이 없다.
- 따라서 로컬 PostgreSQL 상태에 따라 기본 Context 테스트 결과가 달라질 수 있다.

## 영향

- 현재 `./gradlew test` 실패는 Slot 기능 변경으로 발생한 회귀가 아니다.
- 기준선을 복구하기 전에는 새 기능 테스트 실패와 기존 환경 실패를 구분하기 어렵다.
- PostgreSQL 전용 제약과 쿼리를 검증할 재현 가능한 테스트 환경이 없다.

## 권장 해결 방향

PostgreSQL Testcontainers로 통합 테스트 기준선을 만든다.

- 실제 PostgreSQL과 같은 SQL·제약 동작을 검증한다.
- 개발 장비의 로컬 DB 실행 여부에 의존하지 않는다.
- 이후 부분 Unique Index, 동시성 및 실행 계획 검증으로 확장할 수 있다.

Controller Validation처럼 JPA가 필요 없는 테스트는 별도 테스트 슬라이스로
분리해 불필요한 전체 Context와 DB 의존을 줄이는 것도 함께 검토한다.

새 의존성과 테스트 설정은 별도 개발환경 Task에서 추가하고 검증한다.

## 완료 조건

- 로컬 PostgreSQL 실행 여부와 관계없이 `./gradlew test`가 재현된다.
- Context 테스트와 Validation 테스트가 통과한다.
- 테스트가 실제 PostgreSQL 연결을 사용했는지 확인할 수 있다.
- 테스트 실행 방법과 Docker 필요 조건을 README 또는 실행 문서에 기록한다.
