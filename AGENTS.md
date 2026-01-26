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
- 가이드 우선. 방향 제시 전에 2~4개의 핵심 질문을 먼저 한다. (단, 간단·명확한 질문은 1~2개로 축소 가능)
- 사용자가 요청하지 않으면 전체 구현 코드를 작성하지 않는다.
- 단계별 방향, 리스크, 검증 포인트를 제시한다.
- 리뷰 요청 시 버그, 회귀 위험, 테스트 누락을 우선 점검한다.

## 협업 플로우
1) **계약 확정 (Contract First)**
   - 기준 문서: `docs/20_Deliverables/03_API_Specification.md`
   - DTO를 먼저 확정하고, 입력값 검증은 OpenAPI 스펙을 **반영**한다: required, pattern, format, min/max 등
   - 응답 DTO는 OpenAPI 스키마와 1:1로 맞춘다.

2) **관계 스케치 + 스켈레톤 엔티티 (Skeleton)**
   - DTO를 만족하는 **최소 연관관계만** 먼저 설계한다.
   - 이 단계에서는 필드(이름/메모/상태 등)를 넣지 않는다. (@Id + 연관관계만 작성)

3) **스키마 상세화 (Refinement)**
   - 정책/조회 조건/에러 조건을 기준으로 필드를 확정한다.
   - 숨겨진 상태(State) 식별: 승인/삭제/생성일시 등 DB에 저장해야 할 상태값 도출.
   - 제약 조건 설정: Unique Key, Foreign Key 등 DB 정합성 규칙을 정의한다.

4) **도메인 및 레포지토리 계층 구현 (Domain & Repository)**
   - Entity 클래스에 필드를 추가하고 JPA 매핑을 완성한다.
   - Repository 인터페이스 생성 (CRUD + 필요한 Query Method).

5) **비즈니스 로직 설계 및 구현 (Service Layer)**
   - 흐름 제어: 조회 → 검증 → 상태 변경 → 저장.
   - 예외 처리: 정책 위반 시 예외 처리 기준 정의.
   - 트랜잭션 관리: @Transactional 범위 설정.

6) **표현 계층 구현 (Presentation Layer)**
   - Controller 구현: HTTP Method/Endpoint 매핑 + Service 호출.

7) **검증 및 테스트 (Verification)**
   - API 테스트: Postman/Swagger로 응답 JSON이 명세와 일치하는지 확인.
   - 단위 테스트는 가능할 때 선택적으로 수행한다.
   - 개발 플로우 참고: `docs/개발플로우.md`

## Codex 답변 기대치
- 위 문서들을 기준으로 스펙 확인 후 안내한다.
- 요청이 스펙과 충돌하면 먼저 알려주고 문서 업데이트 여부를 묻는다.
- 가능한 단순한 해법부터 제시하고, 필요할 때만 고급 도구를 추천한다.
- 답변은 간결하게, 다음 액션을 포함한다.
- 백엔드 계약을 확정한 뒤 프론트 API 매핑을 진행한다.

## 사용자 입력이 있으면 좋은 정보
- 대상 API 경로 + 메서드.
- OpenAPI 기반 요청/응답 JSON.
- 현재 엔티티/테이블 설계.
- 에러 로그 또는 실패 증상.

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
