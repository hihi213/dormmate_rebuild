---
type: phase
status: 🟡 Doing
period: 01-03 19:30
tags:
  - project
  - phase
---
# 🚩 Phase 2. 로그인/회원 시스템 (Auth)
> *"계정·세션 기반 인증부터 안정적으로"*
* 회원가입/로그인/세션
* 사용자별 물품 격리

## 1. 📋 계약 정의 (Contract Definition)

> **Goal:** UI Flow 및 API 명세를 분석하여 **변경 불가능한 계약(DTO)**을 먼저 확정합니다.
> **Input Source:** `20_Deliverables/03_API_Specification.md`

- [ ] **Target Endpoint:** (예: `GET /fridge/bundles`)
* [ ] **Request DTO:** (예: `BundleCreateRequest` - Validation 포함)
	* *Check:* 프론트엔드 페이로드와 필드명/타입이 100% 일치하는가?
* [ ] **Response DTO:** (예: `BundleResponse` - 화면 출력용)
	* *Check:* DB 구조를 노출하지 않고, 화면에 필요한 데이터만 담았는가?

---

## 3. 🧠 매핑 로직 (Implementation Strategy)

> **Goal:** 위에서 정의한 [1. 계약]과 [2. 설계] 사이의 간극을 메우는 **Service 로직**을 구상합니다.

* **Mapping Method:** (예: `Stream API`를 사용하여 수동 변환 / Builder 패턴 사용)
* **Transaction Scope:** (예: 조회만 하므로 `@Transactional(readOnly = true)` 적용)
* **Query Strategy:**
	* [ ] JPA 기본 메서드 (`findById`) 사용
	* [ ] 복잡한 조회 시 QueryDSL 도입 검토 (Trigger 상황인지 체크)
---

## 3. 🎯 Task 일정 (Priority)

| 필수 기능 1                         | 예상 소요 시간 |
| :------------------------------ | :------- |
| [[Task_<% tp.file.title %>_01]] | 2일       |
| [[Task_<% tp.file.title %>_02]] | 1일       |
| [[Task_<% tp.file.title %>_03]] | 3일       |
| 필수 기능 2                         | 예상 소요 시간 |

| 선택 기능 1                         | 예상 소요 시간 |
| :------------------------------ | :------- |
| [[Task_<% tp.file.title %>_01]] | 2일       |
| [[Task_<% tp.file.title %>_02]] | 1일       |
| [[Task_<% tp.file.title %>_03]] | 3일       |

---

## 4. 🏁 최종 검증 (Verification)
> **계약 준수 여부 확인**
- [ ] **Contract Match:** 실제 API 응답이 위에서 고정한 DTO와 100% 일치하는가?
- [ ] **Architecture:** Controller에는 로직이 없고, Service가 매핑을 전담했는가?
- [ ] **Deliverables:** `20_Deliverables` 폴더 동기화 완료
- [ ] **Decision Log:** 주요 의사결정을 `20_Deliverables/04_Tech_Decisions.md`에 한 줄 기록했는가?
- [ ] **Issue Highlight:** 중요한 트러블슈팅은 `20_Deliverables/05_Issue_Highlights.md`에 요약했는가?
