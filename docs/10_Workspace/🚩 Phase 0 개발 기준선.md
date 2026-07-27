---
type: phase
status: 🟢 Done
scope: MVP
---

# Phase 0 — 개발 기준선

> 기능 구현 전 동일한 런타임과 테스트 결과를 재현할 수 있게 한다.

## 확정

- Java 21 (`.mise.toml`, Gradle Toolchain)
- Node.js 22 (`.mise.toml`, `frontend/package.json` engines)
- npm 10과 `npm ci` 기반 프론트 의존성 재현
- Spring Boot 4.0.1
- Gradle Wrapper 9.2.1
- PostgreSQL 16
- OrbStack Docker 호환 엔진과 Docker Compose 기반 로컬 실행
- PostgreSQL Testcontainers 기반 통합 테스트

## 현재 상태

| Status | Work |
| --- | --- |
| Done | MVP와 Post-MVP API 분류 |
| Done | 문서 역할, Policy Baseline과 Task 흐름 정리 |
| Done | Validation 테스트를 DB 없는 Web MVC 슬라이스로 분리 |
| Done | PostgreSQL Testcontainers 공통 구성 추가 |
| Done | OrbStack 환경에서 전체 `./gradlew test` 통과 |
| Done | mise Node.js 22 환경에서 `npm ci`, 프론트 lint와 build 실행 |
| Known issue | 별도 `tsc --noEmit`은 기존 프론트 타입 오류로 실패하며 Next build는 설정상 타입 검사와 lint를 생략 |

## 완료 조건

- Docker가 실행되는 환경에서 `./gradlew test`가 통과한다.
- ApplicationContext가 PostgreSQL Testcontainer에 연결된다.
- DB가 필요 없는 Web 테스트는 컨테이너 없이 실행된다.
- 테스트 실행 조건이 README 또는 실행 문서에 기록된다.
- 프론트 명령은 `.mise.toml`의 Node.js 22와 잠금 파일 기반 `npm ci`로 재현된다.

## 다음 Phase 선행 조건

인증 전달 방식과 Slot 조회의 OpenAPI 보안 계약은 `AUTH-001`, `AUTH-007`,
`AUTH-008`로 확정했다. 실제 Spring Security 구성과 인증 통합은 개발환경
기준선이 아니라 Phase 1 Slot Task와 Phase 2에서 구현한다.
