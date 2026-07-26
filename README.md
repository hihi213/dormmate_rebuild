# 🏠 DormMate — Backend Rebuild

> **AI 기반 프로토타입으로 검증한 요구사항을 바탕으로, API 계약·도메인 모델·트랜잭션·테스트를 직접 재설계하는 백엔드 리빌드 프로젝트**
>
> **AI가 만든 프로토타입에서, 내가 설명할 수 있는 아키텍처로.**

<div align="center">

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Next.js](https://img.shields.io/badge/Next.js-15.1.9-000000?style=flat-square&logo=nextdotjs&logoColor=white)
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
→ 요청된 경우 프론트 API 연결
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
- Slot 수직 슬라이스 범위의 점진적 ERD 작성, 인증 전략은 검토 전
- 독립된 테스트 DB 구성이 없어 현재 ApplicationContext 로딩과 관련 테스트 실패
- 핵심 사용자 API는 아직 구현 완료 상태가 아님

코드나 OpenAPI 경로가 존재한다는 이유만으로 구현 완료로 표시하지 않습니다. 관련 테스트, 계약, 권한과 주요 실패 흐름까지 검증한 기능만 완료로 판단합니다. API별 범위와 상태는 [API 구현 현황](./docs/10_Workspace/API_Implementation_Status.md)에서 관리합니다.

## 구현 로드맵

OpenAPI는 목표 API의 초기 계약으로 사용하되, 불완전한 항목을 검토·합의한 뒤 구현합니다. 모든 API를 한 번에 구현하지 않습니다.

### Release 1 — Backend MVP

#### 0. 기준선 복구

- Java와 Spring Boot 버전 확정
- 테스트용 DB 환경 구성
- ApplicationContext 테스트 통과
- 공통 오류 응답 기준 확인
- MVP와 Post-MVP API 분류

#### 1. 물품 관리

- 배정된 냉장고 칸 조회
- 포장 등록·조회·수정·소프트 삭제
- 물품 등록·수정·소프트 삭제
- 소유권 검증
- 검사 중 변경 제한
- 검색과 페이징

#### 2. 인증과 권한

- 로그인·로그아웃
- 현재 사용자 조회
- 인증 전달 방식 확정
- 거주자·층별장·관리자 권한
- 본인 데이터 접근 제한

회원가입은 API 계약과 사용자 생성 정책을 확정한 뒤 구현 여부를 결정합니다.

#### 3. 검사 핵심 흐름

- 검사 시작과 대상 조회
- PASS · WARNING · DISPOSE 조치 기록
- 검사 제출
- 중복 시작과 중복 제출 방지
- 제출 이후 변경 금지

#### 4. 최소 관리자

- 사용자 목록
- 전체 물품 조회
- 검사 이력 조회
- 합의된 최소 상태 관리

#### 5. 품질과 연동

- 핵심 단위·통합 테스트
- ProblemDetail 오류 응답
- PostgreSQL 재현 환경
- Docker Compose
- 기존 프론트엔드 연동
- README와 주요 트러블슈팅 정리

### Release 2 — 대표 심화 기능

- 검사 잠금 만료와 연장
- 동시 시작·동시 제출 테스트
- 낙관적·비관적 락 비교
- DB 락과 Redis Lock 비교 실험
- 벌점 생성과 재계산
- 감사 로그
- 알림
- CI/CD
- 성능 측정과 개선

## 기술 스택

현재 저장소에 적용된 버전을 기준으로 작성했습니다. 주요 버전이나 인증 방식 변경은 근거를 기록하고 관련 코드와 문서를 함께 갱신합니다.

### Backend

- Java 17
- Spring Boot 4.0.1
- Spring Data JPA
- Bean Validation
- Gradle 9.2.1
- Spring Security — 인증 전략 확정 후 적용

### Database & Infrastructure

- PostgreSQL 16
- Docker Compose
- Flyway — 핵심 스키마가 안정된 뒤 도입 검토

### Frontend

- Next.js 15.1.9
- React 18
- TypeScript
- 기존 UI와 UX 유지
- 사용자가 요청하면 확정된 백엔드 계약에 맞춰 API Client와 타입을 수정

### Testing

- JUnit 5
- Spring Boot Test
- PostgreSQL 기반 통합 테스트 또는 Testcontainers 검토
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
├── docs/
│   ├── 00_Blueprint/                  # 원본 요구사항과 UI 분석(Read Only)
│   ├── 10_Workspace/                  # Phase, Task, 문제 해결 기록
│   │   ├── Tasks/
│   │   └── Troubleshooting/
│   └── 20_Deliverables/               # API, ERD, 기술 결정 등 최종 산출물
├── AGENTS.md                           # Codex 협업 규칙
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
./gradlew bootRun
```

```bash
cd backend
./gradlew test
```

현재 테스트는 독립된 테스트 DB 구성이 없어 ApplicationContext 로딩 단계에서 실패합니다. 기준선 복구 후 정상 실행 결과를 갱신할 예정입니다.

### 프론트엔드

```bash
cd frontend
npm install
npm run dev
```

```bash
cd frontend
npm run lint
npm run build
```

## 주요 문서

| 문서 | 역할 |
| --- | --- |
| [원본 기능·정책 인벤토리](./docs/00_Blueprint/Feature_Inventory.md) | 기존 요구사항을 보존하고 정책 검토·확정의 1차 근거로 사용 |
| [UI 흐름 분석](./docs/00_Blueprint/UI_Flow_Analysis.md) | 기존 화면과 사용자 흐름 참고 |
| [시스템 아키텍처](./docs/20_Deliverables/01_System_Architecture.md) | 구성요소와 기술 선택 |
| [ERD 및 스키마](./docs/20_Deliverables/02_ERD_&_Schema.md) | 데이터 모델과 제약조건 |
| [API 계약](./docs/20_Deliverables/03_API_Specification.md) | 공개 API 요청·응답의 초기 계약과 합의된 변경 |
| [정책 기준선](./docs/20_Deliverables/06_Policy_Baseline.md) | 확정 정책, 검토 필요 정책과 범위 분류 |
| [API 구현 현황](./docs/10_Workspace/API_Implementation_Status.md) | MVP 범위와 API별 진행 상태 |
| [프론트 연동 기준](./docs/10_Workspace/Frontend_Integration_Baseline.md) | 실제 프론트 동작, 연동 경계와 후속 기술 부채 |
| [기술적 의사결정](./docs/20_Deliverables/04_Tech_Decisions.md) | 합의된 설계와 기술 결정 |
| [면접용 이슈 요약](./docs/20_Deliverables/05_Issue_Highlights.md) | 대표 문제 해결 사례 |
| [Codex 협업 가이드](./AGENTS.md) | 사용자와 AI의 역할 및 작업 규칙 |

## AI 활용 원칙

AI 사용을 숨기거나 금지하는 대신 다음 원칙을 적용합니다.

- AI는 설계 검토, 대안 비교, 코드 리뷰, 테스트 누락 탐색과 오류 분석에 활용합니다.
- 구현 우선순위, 계약 변경, 도메인 모델, 인증, 트랜잭션과 기술 도입은 개발자가 결정합니다.
- AI가 제안하거나 작성한 코드에도 직접 작성한 코드와 동일한 테스트·리뷰 기준을 적용합니다.
- 핵심 기술적 결정과 문제 해결 과정은 문서로 남깁니다.
