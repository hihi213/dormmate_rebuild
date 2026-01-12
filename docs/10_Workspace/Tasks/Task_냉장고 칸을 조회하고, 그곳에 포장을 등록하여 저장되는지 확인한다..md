---
type: task
status: 🟡 Doing
created: 01-03 20:07
tags:
  - task
---
## 1. 🎯 구현 대상 (Scope)

> **Target:** 이번 작업에서 완성할 엔드포인트 목록

- **구현 API:**
    - `GET /fridge/slots` (기초 데이터 조회)
    - `POST /fridge/bundles` (**핵심:** `capacity` 초과 시 422 에러 처리)
    - `GET /fridge/bundles` (목록 조회)
    - `GET /fridge/bundles/{bundleId}` (상세 조회 - Item 목록 포함)
- **Key Point:**
    - `FridgeSlot`, `FridgeBundle` 테이블 생성.
    - `Bundle` 생성 시 `Slot`의 현재 점유량을 `count`하는 로직 구현.

---
## 2. 🔒 계약 정의 (Contract)

> **Step 1:** 필드명/타입변경이 불가능한 외부와 약속한 데이터 규격입니다.
> DB에는 없지만 계산해야하는, 화면에 필요한 필드를 주석으로 표시
### API 01. (API 이름) api별로 복사해 사용

- **Request JSON:**

```JSON
{
  "slotId": "uuid",
  "bundleName": "string"
}
```

- **Response JSON:**

```JSON
{
  "bundleId": "uuid",
  "dDay": "string(D-3)", // <--- (화면필드) 유통기한 기준 가공
  "items": []            // <--- (화면필드) 연관 엔티티 포함
}
```

---
## 3. 🥩 스키마 상세화 (Schema Refinement) `핵심 1`

> **Step 2:** Phase에서 만든 **Entity 뼈대**에 이번 API 구현을 위한 **컬럼(속성)**을 추가합니다.

- 테이블단위로 표 복사해 생성

| **Target Entity** | **Column (Field)** | **Type**     | **Constraint** | **Note**        |
| ----------------- | ------------------ | ------------ | -------------- | --------------- |
| `FridgeBundle`    | `name`             | VARCHAR(120) | NOT NULL       | 포장지 겉면 이름       |
| `FridgeBundle`    | `status`           | VARCHAR(20)  | NOT NULL       | ACTIVE, DELETED |
| `FridgeSlot`      | `capacity`         | INT          | NOT NULL       | 해당 칸의 최대 수용량    |

---

## 4. 🧠 매핑 및 로직 전략 (Mapping & Logic) `핵심 2`

> **Step 3:** DB와 DTO 사이의 간극을 해결하고 비즈니스 규칙을 정의합니다.

### (1) 데이터 가공 및 조합 (Transformation)

|**Target DTO**|**DTO Field**|**Source / Logic**|
|---|---|---|
|`BundleResponse`|`dDay`|`expiryDate` - `now()` 연산 후 "D-N" 포맷팅|
|`BundleResponse`|`items`|`bundle.getItems()` 호출 후 DTO 리스트로 변환|
|`BundleResponse`|`ownerName`|`User` 엔티티에서 `displayName` 가져오기|

### (2) 비즈니스 검증 및 예외 (Validation)

- **Validation 01:** (예: 포장 생성 시 해당 칸의 현재 포장 개수 < `capacity` 인지 확인)
- **Validation 02:** (예: 삭제 시 본인이 소유한 포장인지 확인)
    

---

## 5. 🛠️ 작업 순서 (Action Plan)

> **Step 4:** 고민은 끝났습니다. 이제 기계적으로 코딩하세요.

1. **Entity Update:** 위 [3번]의 테이블 명세를 Entity 클래스에 반영
2. **Repository:** 필요한 쿼리 메서드 추가 (예: `countBySlotId`)
3. **DTO:** API 규격에 맞는 `record` 또는 `class` 생성
4. **Service:** [4번]의 매핑/가공/검증 로직 구현
5. **Controller:** 엔드포인트 연결 및 테스트
    

---

## 6. ✅ 완료 검증 (DoD)

- [ ] **Contract Match:** 모든 API의 응답 JSON이 [2번]의 규격과 일치하는가?
- [ ] **Business Rule:** 허용량 초과 등 예외 상황에서 의도한 에러가 발생하는가?
- [ ] **Persistence:** DB에 데이터가 의도한 타입과 제약조건대로 저장되는가?
- [ ] **Deliverables:** ERD 및 API 명세서 최신화 완료
    - [ ] `20_Deliverables/03_API_Specification.md` 업데이트
    - [ ] `20_Deliverables/02_ERD_&_Schema.md` 업데이트
---

## ⚠️ 백로그 (Later)

- (지금 당장 해결하지 않아도 되는 개선 사항 기록)
