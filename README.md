# 🏠 DormMate (Backend Rebuild)

> **"AI가 짠 코드"에서 "내가 설계한 아키텍처"로.**
> AI 생성물에 의존했던 기존 프로토타입의 한계를 극복하고, **스스로 고민하고 구현하며 엔지니어링 역량을 키우기 위해** 시작한 리빌딩 프로젝트입니다.

<div align="center">

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Obsidian](https://img.shields.io/badge/Docs-Obsidian-483699?style=flat-square&logo=obsidian&logoColor=white)

</div>

## 1. 📖 프로젝트 소개 (Overview)

**DormMate**는 기숙사 내 공용 시설(냉장고, 세탁실 등) 관리의 불편함을 해결하는 웹 서비스입니다.

기존 버전(V1)은 AI 도구를 활용해 빠르게 기획을 검증했으나, 그 과정에서 **제 주관보다는 AI의 기술적 결정에 의존**하는 비중이 컸습니다. 이에 따라 **"내 손으로 직접 구현해야 진짜 내 실력이 된다"**는 판단하에, UI/요구(API)는 고정하고 **DB/도메인 구조는 주도적으로 재설계**하는 프로젝트를 시작했습니다.

* **개발 기간:** 2026.01 ~ (진행 중)
* **개발 인원:** 백엔드 1인 (기존 프론트엔드 리소스 활용)
* **핵심 목표:** AI 의존성 탈피, 레거시 역설계(Reverse Engineering), **Spec-Matching** 구현

## 2. 🎯 프로젝트 배경 & 전략 (Strategy)

단순히 기능을 완성하는 것보다, **이미 완성된 프론트엔드(UI)를 분석하여 백엔드를 역설계하는 과정**에 집중했습니다.

| 구분 | 전략 (Development Strategy) | 비고 |
| --- | --- | --- |
| **Spec-Matching** | UI가 요구하는 JSON 구조(API)를 분석하여 백엔드 구현 | **Gap Analysis** 수행 |
| **Independent DB** | API 요구사항에 끌려다니지 않고, **데이터 무결성을 위한 독자적 DB 설계** | 정규화 & FK 제약조건 |
| **Documentation** | 코드 작성 전 **'설계'**, 작성 후 **'회고'**를 남기는 기록 중심 개발 | Obsidian 활용 |
| **Contract-First** | UI Flow 기준의 API 스펙을 고정 계약으로 보고, 그에 맞춰 구현 | DTO 선확정 → DB 설계/매핑 |

## 3. 🛠️ 아키텍처 및 기술 스택 (Architecture)

### 3-1. 프로젝트 구조

**Frontend(Next.js)**와 **Backend(Spring Boot)**가 통합된 모노레포 구조이며, 문서는 **옵시디언(Obsidian)** 표준 폴더 구조를 따릅니다.

```text
DormMate/                      (Git Root)
├── frontend/                  (📂 Legacy UI: Next.js)
├── backend/                   (📂 Refactored Server: Spring Boot)
├── docs/                      (📂 Project Documentation)
│   ├── 00_Blueprint/          (🛑 기획/분석: UI Flow, 기능 명세)
│   ├── 10_Workspace/          (🏗️ 작업 공간: Phase 노트, 트러블슈팅 로그)
│   ├── 20_Deliverables/       (✨ 최종 산출물: Architecture/ERD/API/Decisions/Issues)
│   └── 99_Assets/             (이미지 리소스)
└── README.md

```

### 3-2. 기술 스택 (Backend)
Backend: Java 17, Spring Boot 4.0.1, Spring Data JPA, Bean Validation
Auth: (예정) Spring Security(세션 기반) + BCrypt
DB: PostgreSQL (로컬은 docker-compose)
Frontend: Next.js(레거시 유지) + 기존 호출 방식 유지(필요한 곳만 최소 수정)
문서/계약: 현재 03_API_Specification.md 고정


### 3-3. 기술 도입 로드맵

|**단계**|**상황 (Trigger)**|**불편함 (Pain Point)**|**해결 기술 (Solution)**|
|---|---|---|---|
|**P1**|복잡한 검색 필터 구현|코드가 지저분하고 오타 발생|**QueryDSL**|
|**P2**|서버 재배포 시 로그아웃|사용자 경험 저하 (데이터 증발)|**Redis (Session)**|
|**P3**|동시 클릭/중복 요청|데이터 정합성 깨짐 (Race Condition)|**Redis (Lock)**|
|**Common**|DB 스키마 변경 누락|배포 시 에러 발생 위험|**Flyway**|
|**Common**|수동 배포의 번거로움|시간 낭비 및 실수 발생|**GitHub Actions**|

👉 **[상세 기술 도입 배경 및 아키텍처 문서 보러가기](./docs/20_Deliverables/01_System_Architecture.md)**


## 4. 📂 개발 문서 (Docs)

소스 코드만으로는 파악하기 힘든 **설계 의도와 기술적 의사결정 배경**을 체계적으로 기록했습니다.
개발 과정은 `Phase(기능 단위) → Task(세부 작업) → Deliverable(산출물)` 순서로 관리됩니다.

### 📚 문서 구조 (Directory Guide)

| 폴더 | 설명 | 주요 포함 내용 |
| --- | --- | --- |
| **[📂 00_Blueprint](./docs/00_Blueprint)** | **절대 기준 (Read Only)** | `UI_Flow_Analysis`, `Feature_Inventory` |
| **[📂 10_Workspace](./docs/10_Workspace)** | **치열한 고민의 흔적** | `Phase_Master_Note`, `Troubleshooting`, `Dev_Logs` |
| **[📂 20_Deliverables](./docs/20_Deliverables)** | **최종 결과물** | `System_Architecture`, `ERD_&_Schema`, `API_Specification`, `Tech_Decisions`, `Issue_Highlights` |

### ⭐ 주요 기록 (Highlights)

* 📄 **[UI Flow 분석 및 API 역설계](./docs/00_Blueprint/UI_Flow_Analysis.md)** : 화면 흐름 참고 문서
* 📄 **[구현된 API 목록](./docs/20_Deliverables/03_API_Specification.md)** : API 계약 기준 문서
* 📄 **[최종 ERD 설계](./docs/20_Deliverables/02_ERD_&_Schema.md)** : 프론트엔드 데이터 구조와 별개로 정규화된 DB 설계
* 📄 **[기술적 의사결정](./docs/20_Deliverables/04_Tech_Decisions.md)** : 기술 스택 선정 및 아키텍처 설계의 근거

## 5. 🚀 핵심 기능 및 로드맵 (Features)

본 프로젝트는 아래 5단계 Phase에 따라 순차적으로 구현됩니다.
*(상세 기능 명세는 [📂 00_Blueprint/Feature_Inventory.md](./docs/00_Blueprint/Feature_Inventory.md)에서 확인 가능합니다.)*

### Phase 1. 물품 관리 (Inventory)

> *"단순화 후 점진 확장: 포장/물품 구조 유지 + 필드 최소화"*

* 포장/물품 CRUD (등록/조회/수정/삭제)
* 목록/상세/등록/수정 화면 중심
* 간단 검색: 포장명 기준

### Phase 2. 로그인/회원 시스템 (Auth)

> *"계정·세션 기반 인증부터 안정적으로"*

* 회원가입/로그인/세션
* 사용자별 물품 격리 

### Phase 3. 냉장고 검사 (Inspection)

> *"검사 세션의 시작-잠금-제출 흐름 구축"*

* 검사 세션 시작/잠금/제출
* 조치 기록(경고/폐기/통과)

### Phase 4. 관리자 기능 (Admin)

> *"운영 조회 중심의 관리자 기능"*

* 전체 물품 조회/검색/삭제
* 검사 이력 조회, 역할 관리


### Phase 5. 알림 유틸리티 (Notification)
> *"푸시 이전 단계: 알림 저장/읽음 처리"*

* 알림 저장/읽음 처리 (Push는 2차 확장)


## 6. 🏃 실행 방법 (How to run)

### OrbStack + Docker Compose

OrbStack을 실행한 뒤 루트 디렉터리에서 전체 스택을 올립니다.

```bash
docker compose up --build -d
```

기본 포트가 이미 사용 중이면 호스트 포트만 바꿔 실행할 수 있습니다.

```bash
FRONTEND_PORT=3001 BACKEND_PORT=8081 POSTGRES_PORT=5433 docker compose up --build -d
```

실행 확인:

```bash
docker compose ps
curl http://localhost:3000/healthz
curl 'http://localhost:8080/debug/errors?status=400'
```

- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

포트를 바꿔 실행했다면 위 주소의 포트도 지정한 값으로 바꿔 접속합니다.

종료:

```bash
docker compose down
```

DB 볼륨까지 초기화하려면:

```bash
docker compose down -v
```

### 로컬 실행

**1️⃣ Backend Server (Port: 8080)**

```bash
cd backend
./gradlew bootRun

```

**2️⃣ Frontend Client (Port: 3000)**

```bash
cd frontend
npm install
npm run dev

```

**3️⃣ 접속**
브라우저에서 `http://localhost:3000`으로 접속하여 테스트 가능.
