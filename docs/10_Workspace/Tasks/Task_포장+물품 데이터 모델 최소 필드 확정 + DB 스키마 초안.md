---
type: task
status: 🟡 Doing
created: 01-03 20:07
tags:
  - task
---
## 1. 📐 계약 및 설계 (Contract & Design)

### (1) DTO 설계 (계약 고정) 🔒
> **Input/Output 스키마 정의 (변경 금지)**
> *UI가 원하는 필드명 그대로 작성*
```json
// Response DTO
{
  "key": "value",
  "desc": "프론트엔드 요구사항 반영"
}
```

### (2) DB 설계 (3정규화) 🛠️

> **데이터 관점의 최적 설계 (DTO와 무관하게 설계)**

Table: 이름

| Column | Type | Key | Constraint | Note |
| :--- | :--- | :--- | :--- | :--- |
| `id` | Long | PK | Auto Inc | |
| `name` | String | | NOT NULL | |

---

## 2. 📝 구현 (Mapping & Logic)

> **핵심:** DB 데이터를 가져와서 DTO로 변환(Mapping)하는 로직 구현
> > **Dirty Code OK.** 처음엔 엉망이어도 돌아가게만 만드세요.
> >**Make it Right:** 코드가 3회 이상 반복되거나 구조가 한계에 다다를 때만 리팩토링합니다.

* **Step 1. Entity & DTO:** 위 설계대로 클래스 파일 생성
* **Step 2. Service Layer:**
	* *Mapping:* `Entity` -> `DTO` 변환 메서드 작성 (MapStruct / 수동)
	* *Logic:* 비즈니스 규칙 적용
* **Step 3. Controller:** 로직 없이 Service 호출 후 DTO 반환

---

## 3. 🧪 계약 검증 (Contract Test)

* [ ] **JSON Structure:** 응답 JSON의 키값이 DTO 설계와 일치하는가?
* [ ] **Type Check:** `Null` 처리가 계약대로 되었는가?
* [ ] **UI Integration:** 프론트엔드 연동 시 에러가 없는가?

---

## 4. ✅ Task Completion

1. **계약 확정:** 검증된 DTO 스펙을 `20_Deliverables/03_API_Specification.md`에 박제
2. **DB 확정:** 최종 ERD를 `20_Deliverables/02_ERD_&_Schema.md`에 박제
3. **상태 업데이트:** Phase Master 체크 완료


## ⚠️ 백로그 (기록하되 미루기)
- [ ] (작업 중 생각난 부가 기능/개선점 격리)
