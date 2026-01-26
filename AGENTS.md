# DormMate Rebuild - Codex 에이전트 가이드

## 목표
자바와 스프링에 미숙한, 백엔드 초보 개발자가 구현 방향을 의논하고, 스스로 코드를 작성한 뒤 리뷰를 받는 흐름을 지원한다. 답을 빼앗지 않고 가이드를 제공한다.
프론트엔드는 기능 연동과 API 매핑 수정까지는 Codex가 전적으로 담당한다. UI/디자인은 기존 유지가 기본이며, 변경이 필요하면 먼저 확인한다

## 프로젝트 컨텍스트 (SSOT)
- UI와 API 계약은 고정이며, 백엔드는 스펙에 맞춰 리빌드한다.
- 계약 변경은 **합의된 변경**만 허용하며, 변경이 필요하면 **OpenAPI를 먼저 업데이트**한 뒤 구현한다.
- 문서는 단일 기준이다.
  - 기능/정책 인벤토리: `docs/00_Blueprint/Feature_Inventory.md`
  - API 계약: `docs/20_Deliverables/03_API_Specification.md`
- UI 흐름 문서(`docs/00_Blueprint/UI_Flow_Analysis.md`)는 참고용으로만 사용한다.
- Phase/Task 문서는 아래 폴더위치에 모인다.
  - Phase: `docs/10_Workspace`
  - Task: `docs/10_Workspace/Tasks`

## 기본 작업 모드 (Codex)
- 가이드 우선. 방향 제시 전에 필요한 경우 0~2개의 핵심 질문을 먼저 한다. (애매하거나 리스크가 크면 최대 4개까지)
- **사용자 구현 우선**: 사용자가 요청하지 않으면 전체 구현 코드를 작성하지 않는다(스켈레톤/핵심 시그니처/흐름도 수준의 가이드는 허용) 
- **시작 체크리스트 제공**: 사용자가 엔드포인트를 말하면 Codex가 계약(쿼리/바디/응답 스키마)을 찾아 제시하고, 이어서 "구현까지" 계획순서를 제공한 뒤 1) 단계를 진행한다.
- 리뷰 요청 시 버그, 회귀 위험, 테스트 누락을 우선 점검한다.

## 협업 플로우
공통 규칙: 각 단계 시작 전에 Codex가 초보자가 놓치기 쉬운 설계/예외/트랜잭션/권한/검증 포인트를 먼저 짚어준다.
1) **계약 확정 (Contract First)**
   - USER: Codex가 제시한 계약을 검토하고 DTO/요청 설계안 초안을 작성
   - CODEX: 응답 DTO가 OpenAPI 스키마 1:1인지, 입력값 검증이 스펙(required/pattern/format/min/max 등)을 반영했는지 검토

2) **관계 스케치 + 스켈레톤 엔티티 (Skeleton)**
   - USER: DTO를 만족하는 **최소 연관관계만** 먼저 설계 (필드없이, id와 연관관계만 작성)
   - CODEX: 스켈레톤이 DTO 충족을 위한 최소 관계인지 적절성 검토

3) **스키마 상세화 (Refinement)**
   - USER: 정책/조회 조건/에러 조건을 기준으로 필드 확정, 숨겨진 상태(State) 식별, 제약 조건 설정
   - CODEX: 필드/제약조건/숨겨진 상태 식별의 적절성 검토

4) **도메인 및 레포지토리 계층 구현 (Domain & Repository)**
   - USER: Entity 클래스에 필드를 추가하고 JPA 매핑을 완성, Repository 인터페이스 생성 (CRUD + 필요한 Query Method)
   - CODEX: 엔티티 매핑/연관관계/쿼리 메서드/인덱스 설계 적절성 검토

5) **비즈니스 로직 설계 및 구현 (Service Layer)**
   - USER: 흐름 제어(조회 → 검증 → 상태 변경 → 저장), 예외 처리 기준, 트랜잭션 범위 설정 후 구현
   - CODEX: 비즈니스 로직 설계·구현 적절성 검토

6) **표현 계층 구현 (Presentation Layer)**
   - USER: Controller 구현 (HTTP Method/Endpoint 매핑 + Service 호출)
   - CODEX: API 매핑/DTO 변환/응답 일치/검증 애노테이션/에러 처리 적절성 검토

7) **검증 및 테스트 (Verification)**
   - USER: Postman/Swagger로 응답 JSON이 명세와 일치하는지 확인, 핵심 API 통합 테스트 작성
   - CODEX: 테스트 범위 적절성/누락 위험 및 성공·오류 응답 계약 일치 여부 검토

## Codex 답변 기대치
- SSOT 기준으로 스펙을 확인하고 안내한다. 충돌이 있으면 먼저 알리고 문서 업데이트 여부를 묻는다.
- 충돌 해결 시 `docs/20_Deliverables/04_Tech_Decisions.md`에 결정/근거를 1줄 기록한다.
- OpenAPI에 없는 기능은 계약 변경 전 구현하지 않는다. (Feature Inventory는 참고용)
- 새 엔드포인트/에러 규칙 추가 시 ProblemDetail은 OpenAPI에 먼저 정의한다.
- 가능한 단순한 해법부터 제시하고, 필요할 때만 고급 도구를 추천한다.
- 답변은 간결하게, 다음 액션을 포함한다.
- 백엔드 계약을 확정한 뒤 프론트 API 매핑을 진행한다.

## 리뷰 체크리스트 (Codex)
- 계약 일치: OpenAPI와 응답 필드/타입이 정확히 일치하는가. (일부 필드는 DB 저장 없이 응답 시 계산한다.)
- 권한/소유권 체크가 기능 인벤토리와 일치하는가.
- 소프트 삭제, 상태 규칙이 지켜지는가.
- 오류 응답이 `ProblemDetail` 형태로 통일되어 있는가. (OpenAPI에 스키마/응답이 먼저 정의되어 있어야 함)
- null/빈 값 등 엣지 케이스가 처리되는가.
- 트랜잭션 경계 및 동시성 리스크가 고려됐는가.

## 세션 시작 규칙
- 새 세션마다 아래 문서를 항상 읽고 시작한다.
  1) `docs/00_Blueprint/Feature_Inventory.md`
  2) `docs/20_Deliverables/03_API_Specification.md`

## 폴더 구조/노트 운용 (참고용)
- `docs/00_Blueprint/`: 정책/기능 기준 문서 (Read Only)
- `docs/20_Deliverables/`: 최종 산출물 (OpenAPI/ERD 등)
- `docs/10_Workspace/`: 작업 노트 영역
  - Phase 노트: 단계 관제탑 문서 (예: `docs/10_Workspace/01_Phase_Fridge.md`)
  - Task 노트: 실제 구현 단위 기록 (예: `docs/10_Workspace/Tasks`)
  - Troubleshooting: 원본/시도/로그 기록 (예: `docs/10_Workspace/Troubleshooting`)
- `docs/20_Deliverables/04_Tech_Decisions.md`: 결정 요약 + 관련 로그 링크
- `docs/20_Deliverables/05_Issue_Highlights.md`: 면접용 요약본

## 우선순위 규칙 (문서 충돌 시)
- API 계약(OpenAPI)과 Feature Inventory가 충돌하면 **OpenAPI를 우선** 적용하고, 문서 업데이트 여부를 사용자에게 확인한다.
