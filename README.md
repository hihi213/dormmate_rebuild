# 🏠 DormMate (Backend Rebuild)

> **"AI가 짠 코드"에서 "내가 설계한 아키텍처"로.**
> AI 생성물에 의존했던 기존 프로토타입의 한계를 극복하고, **스스로 고민하고 구현하며 엔지니어링 역량을 키우기 위해** 시작한 리빌딩 프로젝트입니다.

<div align="center">

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Obsidian](https://img.shields.io/badge/Docs-Obsidian-483699?style=flat-square&logo=obsidian&logoColor=white)

</div>

## 1. 📖 프로젝트 소개 (Overview)

**DormMate**는 기숙사 내 공용 시설(냉장고, 세탁실 등) 관리의 불편함을 해결하는 웹 서비스입니다.

기존 버전(V1)은 AI 도구를 활용해 빠르게 기획을 검증했으나, 그 과정에서 **제 주관보다는 AI의 기술적 결정에 의존**하는 비중이 컸습니다. 이에 따라 **"내 손으로 직접 구현해야 진짜 내 실력이 된다"**는 판단하에, 기존 프론트엔드 UI만 유지하고 **백엔드와 데이터베이스를 주도적으로 재설계(Rebuild)**하는 프로젝트를 시작했습니다.

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

* **Framework:** Spring Boot 3.x, Spring Data JPA
* **Database:** MySQL 8.0 (Local/Prod), H2 (Test)
* **Build:** Gradle
* **Tool:** IntelliJ IDEA, Obsidian (Documentation)

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

* 📄 **[UI Flow 분석 및 API 역설계](./docs/00_Blueprint/UI_Flow_Analysis.md)** : 기존 화면 흐름을 분석하여 백엔드 요구사항을 도출한 문서
* 📄 **[최종 ERD 설계](./docs/20_Deliverables/02_ERD_&_Schema.md)** : 프론트엔드 데이터 구조와 별개로 정규화된 DB 설계
* 📄 **[기술적 의사결정](./docs/20_Deliverables/04_Tech_Decisions.md)** : 기술 스택 선정 및 아키텍처 설계의 근거

## 5. 🚀 핵심 기능 및 로드맵 (Features)

본 프로젝트는 아래 5단계 Phase에 따라 순차적으로 구현됩니다.
*(상세 기능 명세는 [📂 00_Blueprint/Feature_Inventory.md](./docs/00_Blueprint/Feature_Inventory.md)에서 확인 가능합니다.)*

### Phase 1. 물품 관리 (Inventory) - *Current*

> *"Spec-Matching: 프론트엔드 JSON 구조와 DB 정규화 간의 간극 해결"*

* 물품 등록/조회/수정/삭제 (CRUD)
* 냉장/냉동 타입 구분 및 슬롯(Slot) 매핑 전략 수립

### Phase 2. 로그인/회원 시스템 (Auth)

> *"Stateless Architecture: JWT 기반 인증 도입"*

* 사용자별 데이터 격리 (Security Context)
* 기존 UI의 Auth Guard와 연동되는 백엔드 인증 로직 구현

### Phase 3. 냉장고 검사 (Inspection)

> *"Complex Business Logic: 검사 세션 및 이력 관리"*

* 층별장 전용 검사 프로세스 구현 (상태 머신)
* 위반 물품 조치(폐기/경고) 트랜잭션 처리

### Phase 4. 관리자 기능 (Admin)

> *"Operational Excellence: 운영 효율화"*

* 칸 재배분 시뮬레이션 로직
* 데이터 감사(Audit) 로그 및 운영 대시보드 API

### Phase 5. 알림 유틸리티 (Notification)
> *"Async Processing: 비동기 알림 처리"*

* 유통기한 임박 및 검사 결과 알림
* Polling vs SSE(Server-Sent Events) 기술 검토 및 적용

## 6. 🏃 실행 방법 (How to run)

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
