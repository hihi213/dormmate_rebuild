---
type: phase
status: 🟡 Doing
period: 01-03 19:31
tags:
  - project
  - phase
---
# 🚩 Phase 3 _ 냉장고 검사 (Inspection)

> *"검사 세션의 시작-잠금-제출 흐름 구축"*

* 검사 세션 시작/잠금/제출
* 조치 기록(경고/폐기/통과)

### 🤝 API 계약 고정 (Immutable Contract)
> **변경 불가:** `00_Blueprint/UI_Flow_Analysis.md`의 요구사항을 기반으로 엔드포인트와 필드명을 확정합니다.

- [ ] **Endpoint:** (예: `GET /fridge/bundles`)
- [ ] **DTO Policy:** (예: `BundleResponse`는 `items` 리스트를 포함하는 중첩 구조)
- [ ] **Constraint:** (예: 필드명은 camelCase, 날짜는 ISO-8601 준수)

---

## 2. 🧠 매핑 전략 (Mapping Strategy)
> **Core Logic:** 고정된 계약(DTO)과 이상적인 DB(3NF) 사이의 간극을 어떻게 메울 것인가?

- **DB 설계 방향:** (예: 데이터 무결성을 위해 `Bundle`과 `Item` 완전 분리)
- **Mapping Strategy:**
    - **Read:** (예: QueryDSL Projections 사용하여 DTO로 즉시 변환)
    - **Write:** (예: Service 레이어에서 `BundleDTO`를 분해하여 2개의 Entity로 저장)
- **Critical Point:** (예: N+1 문제 방지를 위한 `BatchSize` 적용)

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