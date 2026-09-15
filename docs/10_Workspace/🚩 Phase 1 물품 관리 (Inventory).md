---
type: phase
status: 🟡 Doing
scope: MVP
---

# Phase 1 — 물품 관리

> 사용자가 배정된 냉장고 칸을 조회하고 자신의 포장과 물품을 안전하게 관리할 수 있게 한다.

## 범위

- 배정된 냉장고 칸 조회
- 포장 등록·조회·수정·소프트 삭제
- 물품 추가·수정·소프트 삭제
- 소유권과 Slot 운영 상태 검증
- 검색과 페이징
- 라벨·용량 정합성과 동시 요청 검증
- 대표 조회의 권한·삭제 조건, 쿼리 수와 실행 계획 검증

검사 중 변경 제한은 Phase 3에서 잠금 원천과 상태 전이를 확정한 뒤 Bundle·Item
변경 API에 추가한다. Phase 1 완료를 Phase 3 구현에 의존시키지 않는다.

## 현재 수직 슬라이스

| Status | Task | API |
| --- | --- | --- |
| Review Required | [Slot 조회 및 검증](./Tasks/Task_Slot%20조회%20및%20검증.md) | `GET /fridge/slots` |
| Not Started | Bundle 목록·생성 | `GET/POST /fridge/bundles` |
| Not Started | Bundle 상세·수정·삭제 | `GET/PATCH/DELETE /fridge/bundles/{bundleId}` |
| Not Started | Item 관리 | `POST /fridge/bundles/{bundleId}/items`, `PATCH/DELETE /fridge/items/{itemId}` |

## 작업 방식

각 Task는 다음 순서로 필요한 범위만 설계한다.

```text
Policy Baseline
→ OpenAPI
→ 데이터·생명주기·제약 스케치
→ 테스트 시나리오
→ 구현
→ 계약·권한·실패 흐름 검증
→ 문서 상태 갱신
```

전체 ERD나 JPA 관계를 Phase 시작 시 미리 확정하지 않는다. 현재 수직 슬라이스에 필요한 모델만 설계하고 `02_ERD_&_Schema.md`를 점진적으로 확장한다.

## 완료 조건

- 물품 관리 MVP API가 계약과 확정 정책에 일치한다.
- 다른 사용자의 데이터 접근이 차단된다. 퇴역 Slot의 조회·신규 변경 제약은 `INV-023`의 미결 정책을 확정한 뒤 검증한다.
- 소프트 삭제 데이터가 일반 조회에서 제외된다.
- 정상 흐름과 주요 실패 흐름 테스트가 통과한다.
- 확정 계약의 필수 프론트 API 매핑과 주요 사용자 흐름을 검증한다.
- 1차 배포·운영 검증은 [README 완료 기준](../../README.md#1차-완료-기준)을 따른다.
