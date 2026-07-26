# 테스트 DB 기준선 부재

> Status: Resolved
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

## 적용한 해결

- Spring Boot Testcontainers와 Testcontainers PostgreSQL/JUnit Jupiter 의존성 추가
- PostgreSQL 16 Alpine 컨테이너를 `@ServiceConnection`으로 등록
- Context 테스트에 공통 컨테이너 설정 연결
- DB가 필요 없는 Validation 테스트를 Web MVC 슬라이스로 분리

확인 결과:

```text
./gradlew test --tests com.dormmate.ProblemDetailValidationTest
BUILD SUCCESSFUL
```

Codex의 기본 샌드박스에서는 OrbStack 사용자 소켓 접근 권한이 없어 컨테이너
시작 단계에서 실패했다. OrbStack 자체는 정상 실행 중이었으며 실제 사용자
환경 권한으로 다시 실행하자 전체 테스트가 통과했다.

```text
orb status
Running

./gradlew test
BUILD SUCCESSFUL
```

따라서 기존 Hibernate Dialect 실패는 Testcontainers 구성으로 해결됐다.
Codex가 OrbStack을 사용하는 Docker/Testcontainers 명령을 실행할 때는
`~/.orbstack/run/docker.sock` 접근을 위한 권한 승격이 필요할 수 있다.

## 선택 근거

PostgreSQL Testcontainers로 통합 테스트 기준선을 만든다.

- 실제 PostgreSQL과 같은 SQL·제약 동작을 검증한다.
- 개발 장비의 로컬 DB 실행 여부에 의존하지 않는다.
- 이후 부분 Unique Index, 동시성 및 실행 계획 검증으로 확장할 수 있다.

Controller Validation처럼 JPA가 필요 없는 테스트는 별도 테스트 슬라이스로
분리해 불필요한 전체 Context와 DB 의존을 줄이는 것도 함께 검토한다.

## 완료 조건

- 로컬 PostgreSQL 실행 여부와 관계없이 `./gradlew test`가 재현된다.
- Context 테스트와 Validation 테스트가 통과한다.
- 테스트가 실제 PostgreSQL 연결을 사용했는지 확인할 수 있다.
- 테스트 실행 방법과 Docker 필요 조건을 README 또는 실행 문서에 기록한다.

## 최종 결과

- PostgreSQL 16 Testcontainer 연결 확인
- `BackendApplicationTests.contextLoads()` 통과
- `ProblemDetailValidationTest` 통과
- 로컬 PostgreSQL 실행 여부에 의존하지 않는 테스트 기준선 확보
