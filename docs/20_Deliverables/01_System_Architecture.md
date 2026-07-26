# System Architecture

> 현재 저장소에서 확인된 구성과 후속 학습 후보를 구분한다. 후보 기술을 현재 사용 중인 구성처럼 표시하지 않는다.

## 1. Current Architecture

```mermaid
flowchart LR
    User[Browser] --> Frontend[Next.js 15]
    Frontend -->|REST JSON| Backend[Spring Boot 4]
    Backend -->|JPA / JDBC| PostgreSQL[(PostgreSQL 16)]
```

| Component | Current role | Status |
| --- | --- | --- |
| Next.js | 기존 UI·UX와 API Client | Existing |
| Spring Boot | Rebuild 대상 REST API | Skeleton / In Progress |
| Spring Data JPA | 영속성 접근 | Existing |
| PostgreSQL | 운영·로컬 데이터 저장 | Existing |
| Docker Compose | 로컬 실행 환경 | Existing |

현재 인증 방식은 `AUTH-001`의 `Review Required` 상태다. 프론트는 Bearer access/refresh token을 사용하지만, Rebuild가 이를 채택한다는 의미는 아니다.

## 2. Request Lifecycle

```text
HTTP 요청
→ Controller: 바인딩·입력 검증
→ Service: 권한·소유권·상태 검증과 트랜잭션
→ Repository: 필요한 데이터 조회·저장
→ DTO 매핑
→ HTTP 응답
```

- Entity와 API DTO는 분리한다.
- Repository 구현은 Spring Data JPA와 명시적 쿼리부터 검토한다.
- 데이터 무결성은 애플리케이션 검증과 DB 제약조건을 함께 사용한다.
- 수직 슬라이스에 필요한 모델과 쿼리만 추가한다.

## 3. Current Development Decisions

### Schema Management

- 현재 개발 환경은 `spring.jpa.hibernate.ddl-auto=update`를 사용한다.
- 초기 모델 탐색을 위한 임시 설정이며 배포용 스키마 관리 전략으로 간주하지 않는다.
- 핵심 MVP 스키마가 안정되면 Flyway 초기 마이그레이션을 작성하고 `ddl-auto=validate`로 전환한다.
- 테스트 DB에서도 운영과 동일한 마이그레이션을 재현하는 것을 목표로 한다.

### DTO Mapping

- MVP에서는 MapStruct 없이 명시적인 수동 매핑을 사용한다.
- Entity와 API DTO, 계산 필드와 권한별 응답 차이를 직접 드러낸다.
- 반복 매핑이 실제 유지보수 문제로 확인되면 자동 매핑 도구를 재검토한다.

### Local Reproducibility

- Docker Compose로 프론트엔드, 백엔드와 PostgreSQL 실행 조건을 재현한다.
- PostgreSQL 버전, 환경 변수와 서비스 연결을 코드로 관리한다.
- Docker Compose 선택은 클라우드 또는 운영 배포 환경 선택과 별개의 결정이다.

## 4. Candidate Architecture

다음 기술은 확정 구성이나 현재 의존성이 아니다.

| Candidate | Trigger | 먼저 검증할 방법 |
| --- | --- | --- |
| Redis Session | 세션 인증 확정 후 재시작·다중 서버에서 세션 공유 필요 | 단일 서버 세션 요구와 배포 구조 확인 |
| Redis Lock | 다중 인스턴스에서 DB 락만으로 검사 동시성을 해결하기 어려움 | 유일성 제약, 조건부 갱신, 낙관적·비관적 락 |
| QueryDSL | 검색 조건 조합이 복잡해지고 쿼리 유지보수가 어려움 | Query Method, JPQL, Specification |
| Flyway | 핵심 스키마가 안정되고 변경 이력 재현이 필요 | 현재 스키마와 초기 마이그레이션 기준 확정 |
| SSE | 다중 층별장 실시간 검사 합류를 구현 | 폴링 기반 MVP와 동시성 정책 검증 |
| GitHub Actions | 로컬 검증 명령이 안정되고 반복 자동화가 필요 | 재현 가능한 테스트·lint·build 기준선 |

## 5. Redis 학습 계획

Redis는 Post-MVP 비교 실험으로 남긴다.

```text
DB 제약과 트랜잭션
→ 낙관적·비관적 락
→ 동시 요청 테스트와 한계 측정
→ Redis Lock 구현
→ 복잡도·정합성·운영 비용 비교
```

Redis Session은 Token 인증을 선택하면 프로젝트 인증 문제의 직접 해법이 아닐 수 있으므로 인증 결정 이후 별도로 판단한다.

## 6. 검증되지 않은 구성

- Redis, QueryDSL, Flyway는 현재 Backend 의존성에 없다.
- 독립 테스트 DB가 없어 현재 ApplicationContext 테스트가 실패한다.
- OpenAPI의 공통 인증 보안 계약은 아직 확정되지 않았다.
- 프론트 lint와 build는 로컬 Node 환경 부재로 최근 검증하지 못했다.
