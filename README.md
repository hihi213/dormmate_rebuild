# 🏠 DormMate — Backend Rebuild

> **AI 기반 프로토타입으로 검증한 요구사항을 바탕으로, API 계약·도메인 모델·트랜잭션·테스트를 직접 재설계하는 백엔드 리빌드 프로젝트**
>
> **AI가 만든 프로토타입에서, 내가 설명할 수 있는 아키텍처로.**

<div align="center">

![Java](https://img.shields.io/badge/Java-21-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Next.js](https://img.shields.io/badge/Next.js-15.5.22-000000?style=flat-square&logo=nextdotjs&logoColor=white)
![Status](https://img.shields.io/badge/Status-Backend%20Rebuilding-orange?style=flat-square)

</div>

## 프로젝트 소개

**DormMate**는 기숙사 냉장고 물품과 층별 검사를 관리하기 위한 웹 서비스입니다.

기존 버전은 AI 도구를 활용해 프론트엔드와 주요 사용자 흐름을 빠르게 구현하고 서비스 아이디어를 검증한 프로토타입입니다. 이번 Rebuild에서는 기존 UI와 요구사항을 활용하되, 백엔드의 API 계약과 도메인 구조를 직접 분석하고 다시 설계합니다.

재설계 대상은 다음과 같습니다.

- API 요청·응답 계약
- 도메인과 데이터 모델
- 권한과 소유권
- 상태 전이와 삭제 정책
- 데이터 무결성과 트랜잭션 경계
- 오류 응답과 테스트 전략
- 기존 프론트엔드와의 실제 연동

AI는 설계 검토, 코드 리뷰, 테스트 누락 탐색과 오류 분석에 활용합니다. 핵심 설계, 백엔드 구현, 기술 선택의 최종 판단은 개발자가 담당합니다.

| 구분 | 내용 |
| --- | --- |
| 개발 형태 | 백엔드 1인 리빌드 |
| 프론트엔드 | 기존 Next.js UI·UX 유지 |
| 현재 상태 | 백엔드 초기 스켈레톤 및 계약 분석 |
| 개발 방식 | Contract-First · Policy-Driven · Vertical Slice |
| 핵심 목표 | AI의 제안을 검증하고 기술적 결정을 설명할 수 있는 백엔드 역량 확보 |

## 리빌드 전략

| 원칙 | 적용 방식 |
| --- | --- |
| **Contract-First** | 구현 전에 Method, Path, 요청·응답 DTO와 오류 계약을 확인합니다. |
| **Policy-Driven** | 원본 인벤토리는 보존하고, 합의된 정책은 Policy Baseline에서 상태와 함께 관리합니다. |
| **Independent Domain Model** | API DTO를 Entity로 복사하지 않고 데이터 무결성과 생명주기를 기준으로 모델링합니다. |
| **Vertical Slice** | 계층별로 넓게 만드는 대신 엔드포인트 또는 사용자 흐름 하나를 끝까지 완성합니다. |
| **Test Before Expansion** | 정상·실패 시나리오를 정하고 검증된 기능만 다음 단계로 확장합니다. |
| **Simple First** | 복잡한 기술보다 DB 제약조건, 트랜잭션과 기본 JPA를 먼저 검토합니다. |
| **Decision Ownership** | AI의 제안은 검토 대상으로 사용하고 최종 결정과 코드 채택은 개발자가 담당합니다. |

기본 개발 흐름은 다음과 같습니다.

```text
작업 정의
→ 계약·정책 확인
→ 도메인·데이터 모델 스케치
→ 테스트 시나리오 정의
→ 도메인·영속성 구현
→ 서비스·표현 계층 구현
→ 통합 테스트
→ 확정 계약의 필수 프론트 API 연결
→ 회고·문서 갱신
```

단순 조회처럼 위험이 낮은 작업은 일부 단계를 통합할 수 있지만 계약, 권한·소유권, 완료 검증은 생략하지 않습니다. 자세한 협업 방식은 [AGENTS.md](./AGENTS.md)를 따릅니다.

## 현재 진행 상태

현재 백엔드는 초기 재설계 단계입니다. 아래 로드맵에는 구현 완료 기능과 계획된 범위가 함께 포함되어 있습니다.

- 기존 프론트엔드 UI와 주요 사용자 흐름 존재
- 원본 기능 인벤토리, 정책 기준선과 초기 OpenAPI 계약 존재
- OpenAPI의 인증 요구, 필수 필드, 응답 미디어 타입과 오류 계약은 구현 전에 보완 필요
- `User`, `FridgeSlot`, `FridgeBundle`, `FridgeItem` 스켈레톤 존재
- 첫 번째 수직 슬라이스인 냉장고 칸 조회 설계 진행 중
- Slot 수직 슬라이스 범위의 점진적 ERD 작성, 서버 세션 인증 전략 확정·구현 전
- PostgreSQL Testcontainers 기준선과 과거 통과 기록 존재. 최근 재현 결과는 [Phase 0](./docs/10_Workspace/🚩%20Phase%200%20개발%20기준선.md) 참조
- 핵심 사용자 API는 아직 구현 완료 상태가 아님

코드나 OpenAPI 경로가 존재한다는 이유만으로 구현 완료로 표시하지 않습니다. 관련 테스트, 계약, 권한과 주요 실패 흐름까지 검증한 기능만 완료로 판단합니다. API별 범위와 상태는 [API 구현 현황](./docs/10_Workspace/API_Implementation_Status.md)에서 관리합니다.

## 구현 로드맵

OpenAPI는 목표 API의 초기 계약으로 사용하되, 불완전한 항목을 검토·합의한 뒤 구현합니다. 모든 API를 한 번에 구현하지 않습니다.

### Release 1 — 1차 완료 범위

목표는 6~8주 안에 작은 사용자 흐름을 구현·검증·배포하고 직접 설명할 수 있는
백엔드 문제 해결 경험을 남기는 것이다. 상세 API 범위와 진행 상태는
[API 구현 현황](./docs/10_Workspace/API_Implementation_Status.md)에서 관리한다.

- 세션 로그인·로그아웃·CSRF·현재 사용자 조회
- 현재 호실에 배정된 Slot 접근과 조회
- 포장·물품 등록·조회·수정·소프트 삭제, 검색과 페이징
- 소유권과 작성자 메모 보호
- 라벨 중복·용량 초과 방지와 동시 요청 검증
- 권한·삭제 조건을 지키는 대표 조회의 쿼리 수·실행 계획 확인
- 필수 프론트 연동, 테스트·타입 검사·lint·build와 CI
- 재현 가능한 DB 마이그레이션, Docker 배포, HTTPS·로그·백업 복구 확인

계정 공급 방식, 라벨 재사용 등 미결 정책은 해당 Task에서 확정한다. 검사 잠금은
검사 확장 때 추가하며, 1차 물품 관리 완료를 검사 구현에 의존시키지 않는다.

### 1차 완료 기준

- fixture 없이 실제 DB로 로그인 → Slot 조회 → 포장·물품 관리 → 로그아웃이 동작한다.
- 계약, 소유권·호실 접근, 주요 실패 흐름과 라벨·용량 동시성을 테스트로 검증한다.
- 대표 조회의 쿼리와 측정 조건을 설명할 수 있다. 개선은 측정 근거가 있을 때 수행한다.
- 마이그레이션으로 스키마를 재현하고 CI에서 관련 검증 실패를 감지한다.
- 배포 환경의 HTTPS, 오류 로그, 재시작 후 데이터 보존, 백업·복구와 이전 버전 복귀를 확인한다.
- 미지원 기능을 실제 구현처럼 노출하지 않는다. 필요한 UI 변경은 별도 합의한다.
- 실제 구현 범위와 핵심 선택·검증 근거를 관련 문서에 기록한다.

### 이후 확장 원칙

1차 범위의 구현·검증·배포를 완료한 뒤, [Feature Inventory](./docs/00_Blueprint/Feature_Inventory.md)에
보존된 나머지 기능을 순차적으로 확장한다. 후속으로 분리한 기능은 폐기하지 않는다.

확장 범위와 순서는 기존 Phase 계획, 기능 간 의존성, 실제 사용·운영 피드백을
바탕으로 정한다. Policy Baseline의 확정 정책은 유지하고, 미결 정책과 기존
OpenAPI 계약은 각 확장 Task를 시작할 때 검토한다.

검사, 관리자, 알림, 거주 이동·재배분 및 나머지 기능은 후속 범위로 보존한다.
Phase 번호는 문서 식별이며 반드시 실행 순서를 뜻하지 않는다. 1차 인증·물품
흐름처럼 선행 관계에 따라 필요한 작업을 연결해 진행한다.

기술은 확장 기능에 필요한 문제와 검증 근거를 바탕으로 선택하며, 기존 문서에
등장한다는 이유만으로 도입하지 않는다. Redis, SSE 등은 자동 도입 대상이 아니다.

## 기술 스택

현재 저장소에 적용된 버전을 기준으로 작성했습니다. 주요 버전이나 인증 방식 변경은 근거를 기록하고 관련 코드와 문서를 함께 갱신합니다.

### Backend

- Java 21 — 저장소 `.mise.toml`과 Gradle Toolchain으로 통일
- Spring Boot 4.0.1
- Spring Data JPA
- Bean Validation
- Gradle 9.2.1
- Spring Security — 서버 세션·CSRF 전략 확정, 구현 예정

### Database & Infrastructure

- PostgreSQL 16
- Docker Compose
- Flyway — 배포 전 마이그레이션 재현을 위해 도입 예정, 현재 미적용

### Frontend

- Next.js 15.5.22
- React 18
- TypeScript
- 기존 UI와 UX 유지
- 확정 계약에 따른 필수 API 연동은 [공통 협업 규칙](./AGENTS.md)에 따라 수행

### Testing

- JUnit 5
- Spring Boot Test
- PostgreSQL 16 Testcontainers 기반 통합 테스트
- OrbStack의 Docker 호환 엔진 사용
- mise 기반 Java 런타임 관리
- 프론트 lint 및 build
- 핵심 사용자 흐름 검증

## 기술 도입 원칙

새로운 기술은 사용 자체가 아니라 현재 문제를 해결할 필요가 있을 때 도입합니다.

| 문제 또는 조건 | 우선 검토 | 후속 선택지 |
| --- | --- | --- |
| 복잡한 동적 검색 | 명시적 쿼리 또는 Specification | QueryDSL |
| 중복 생성·제출 | DB 유일성 제약과 트랜잭션 | 낙관적·비관적 락 |
| 단일 서버 동시성 | DB 락과 조건부 상태 변경 | Redis Lock 필요성 검토 |
| 서버 재시작 후 세션 유지 | 인증 요구사항과 배포 구조 | Redis Session |
| 스키마 변경 관리 | 변경 내역과 스키마 안정화 | Flyway |
| 반복되는 수동 검증 | 로컬 검증 명령 정리 | GitHub Actions |

Redis와 분산 락은 DB 제약조건, 트랜잭션과 JPA 락으로 해결하기 어려운 근거가 생긴 뒤 검토합니다.

## 프로젝트 구조

```text
DormMate/
├── frontend/                          # 기존 Next.js 프론트엔드
├── backend/                           # Spring Boot 백엔드 리빌드
├── .mise.toml                         # 프로젝트 Java 런타임 선언
├── docs/
│   ├── 00_Blueprint/                  # 원본 요구사항과 UI 분석(Read Only)
│   ├── 10_Workspace/                  # Phase, Task, 문제 해결 기록
│   │   ├── Tasks/
│   │   └── Troubleshooting/
│   └── 20_Deliverables/               # API, ERD, 기술 결정 등 최종 산출물
├── AGENTS.md                           # 공통 AI 협업 규칙
├── docker-compose.yml
└── README.md
```

## 실행 및 검증

### Docker Compose

전체 스택을 실행합니다.

```bash
docker compose up --build -d
```

기본 포트를 사용할 수 없다면 호스트 포트를 변경할 수 있습니다.

```bash
FRONTEND_PORT=3001 \
BACKEND_PORT=8081 \
POSTGRES_PORT=5433 \
docker compose up --build -d
```

상태와 기본 엔드포인트를 확인합니다.

```bash
docker compose ps
curl http://localhost:3000/healthz
curl 'http://localhost:8080/debug/errors?status=400'
```

기본 접속 주소:

- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

종료:

```bash
docker compose down
```

다음 명령은 DB 데이터를 함께 삭제하므로 초기화가 필요할 때만 실행합니다.

```bash
docker compose down -v
```

### 백엔드

```bash
cd backend
mise exec -- ./gradlew bootRun
```

```bash
cd backend
mise exec -- ./gradlew test
```

백엔드 전체 테스트는 OrbStack의 Docker 호환 엔진과 PostgreSQL 16
Testcontainers를 사용합니다. 테스트 실행 전에 OrbStack이 실행 중이어야 합니다.

### 프론트엔드

```bash
cd frontend
mise exec -- npm ci
mise exec -- npm run dev
```

```bash
cd frontend
mise exec -- ./node_modules/.bin/tsc --noEmit
mise exec -- npm run lint
mise exec -- npm run build
```

## 주요 문서

| 문서 | 역할 |
| --- | --- |
| [원본 기능·정책 인벤토리](./docs/00_Blueprint/Feature_Inventory.md) | 기존 요구사항을 보존하고 정책 검토·확정의 1차 근거로 사용 |
| [UI 흐름 분석](./docs/00_Blueprint/UI_Flow_Analysis.md) | 기존 화면과 사용자 흐름 참고 |
| [시스템 아키텍처](./docs/20_Deliverables/01_System_Architecture.md) | 시스템 구성요소와 연결 구조 |
| [ERD 및 스키마](./docs/20_Deliverables/02_ERD_&_Schema.md) | 데이터 모델과 제약조건 |
| [API 계약](./docs/20_Deliverables/03_API_Specification.md) | 공개 API 요청·응답의 초기 계약과 합의된 변경 |
| [정책 기준선](./docs/20_Deliverables/06_Policy_Baseline.md) | 확정 정책, 검토 필요 정책과 범위 분류 |
| [API 구현 현황](./docs/10_Workspace/API_Implementation_Status.md) | MVP 범위와 API별 진행 상태 |
| [프론트 연동 기준](./docs/10_Workspace/Frontend_Integration_Baseline.md) | 실제 프론트 동작, 연동 경계와 후속 기술 부채 |
| [기술적 의사결정](./docs/20_Deliverables/04_Tech_Decisions.md) | 합의된 설계와 기술 결정 |
| [단계별 Phase](./docs/10_Workspace/) | 단계 목표·범위·완료 조건, Task 구성과 전체 진행 상황 관리 |
| [작업별 Task](./docs/10_Workspace/Tasks/) | 구체적인 작업의 범위·설계 합의·구현 순서·진행 상황·완료 검증 관리 |
| [주제별 Troubleshooting](./docs/10_Workspace/Troubleshooting/) | 면접 대비용 실제 문제 해결 경험: 문제·생각과 판단·해결 행동·확인 결과 |
| [면접용 이슈 요약](./docs/20_Deliverables/05_Issue_Highlights.md) | 대표 문제 해결 사례 |
| [공통 AI 협업 가이드](./AGENTS.md) | 사용자와 AI의 역할 및 작업 규칙 |

Phase는 단계 전체의 계획과 현황을, Task는 개별 작업의 실행을 관리합니다.
설계 합의는 해당 Task에 결정 시점부터 남기고 실제 검증 후 결과를 보완합니다.
단순 설계 고민은 Task에 남기고, 면접에서 설명할 문제 해결 경험은 주제별
Troubleshooting에 “어떤 문제가 있었고, 어떻게 생각해서, 어떻게 해결하고 확인했는가”로
정리합니다. 미해결·검증 전 기록은 해결 사례와 구분하며, 대표 사례만 Issue Highlights에
요약합니다. 기록 방법의 원문은 [공통 협업 규칙](./AGENTS.md#8단계--기록-정리와-회고)을 따릅니다.

## AI 활용 원칙

AI 사용을 숨기거나 금지하는 대신 다음 원칙을 적용합니다.

- AI는 설계 검토, 대안 비교, 코드 리뷰, 테스트 누락 탐색과 오류 분석에 활용합니다.
- 개발자가 먼저 초안과 근거를 제시하고, AI는 누락 조건·반례·검증 방법을 중심으로 검토합니다.
- 필요한 개념과 힌트는 제공하되, 대안·추천 구현은 요청받았을 때 제공합니다.
- 구현 우선순위, 계약 변경, 도메인 모델, 인증, 트랜잭션과 기술 도입은 개발자가 결정합니다.
- AI가 제안하거나 작성한 코드에도 직접 작성한 코드와 동일한 테스트·리뷰 기준을 적용합니다.
- 핵심 기술적 결정과 문제 해결 과정은 문서로 남깁니다.
