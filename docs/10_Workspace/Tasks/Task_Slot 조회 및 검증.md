---
type: task
status: 🟡 Doing
created: 01-03 20:07
tags:
  - task
---
> Target API 상세:

- **Request JSON:**

```JSON
{
  "slotId": "uuid", //docs/20_Deliverables/03_API_Specification.md에서 복사해 붙여넣기
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

## 1. 🥩 [Step 3] 실무 구현: 살 붙이기 (Implementation)

**목표:** Phase 1에서 만든 뼈대(Skeleton)와 계약(DTO)을 바탕으로 실제 작동하는 코드를 작성합니다.

### 1-1. 스키마 상세화 (Schema Refinement)

> **Question:** API의 필터링(`status`), 에러 처리(`capacity`), 정렬(`createdAt`)을 위해 **Entity에 어떤 컬럼이 추가되어야 하나요?**

|**Entity**|**필드명**|**타입**|**필수여부**|**추가 사유 (Validation/Logic)**|
|---|---|---|---|---|
|`Bundle`|`status`|Enum|Y|삭제된 꾸러미를 제외하고 조회하기 위해|
|`Slot`|`capacity`|Integer|Y|물품이 꽉 찼는지(Max) 검증하기 위해|
|`Item`|`expiryDate`|LocalDate|N|D-Day 계산을 위한 원천 데이터|

### 1-2. 매핑 및 로직 설계 (Strategy)

> **Mapping:** DTO의 데이터를 Entity로 바꿀 때, 혹은 그 반대일 때의 규칙을 정합니다.

- **Request 핸들링:**
	- `BundleCreateRequest` → `Bundle`
    - 입력받은 `slotId`로 `SlotRepository`를 조회하여 영속성 객체를 찾는다.
    - `Bundle.create(slot, request.name)` 정적 팩토리 메서드를 사용해 생성한다.
- **Response 핸들링:**
	- `Bundle` → `BundleResponse`
    - `dDay`는 DB에 없으므로 `ChronoUnit.DAYS.between()`을 사용하여 계산 후 DTO에 담는다.

> **Business Logic:** "데이터를 저장하기 전/후에 무엇을 체크해야 하는가?"

1. **사전 검증:** `Slot`이 존재하지 않으면 `ResourceNotFoundException`.
2. **핵심 로직:** `Slot.currentItems` >= `Slot.capacity` 이면 `CustomException(FULL_SLOT)` 발생.
3. **후처리:** 저장 후 `CreateEvent` 발행 (선택사항).

### 1-3. 기계적 구현 (Action Checklist)

> **Execution:** 위 설계가 끝났으므로 고민 없이 순서대로 코딩합니다.

- [ ] **Entity:** 위 1-1에서 정의한 필드(`status`, `capacity`…) 추가
- [ ] **DTO:** `Request`/`Response` 클래스 생성 (Validation 어노테이션 포함)
- [ ] **Repository:** 필요한 쿼리(`findAllByStatus`, `findBySlotId` 등) 인터페이스 작성
- [ ] **Service:** 1-2의 매핑 및 비즈니스 로직 구현 (`@Transactional` 적용)
- [ ] **Controller:** URL 매핑 및 Service 호출 연결
    

---

## 2. ✅ [Step 4] 검증 및 마감 (Closing)

**목표:** 구현 결과를 확인하고, 변경된 내용을 문서에 반영하여 '완료(Done)' 상태로 만듭니다.

### 2-1. 결과 검증 (Verification)

- [ ] **Postman:** Request JSON을 보냈을 때, 정의한 Response Spec과 100% 일치하는가?
- [ ] **DB Check:** 데이터 저장 시 `parent_id`(FK)가 올바르게 들어갔는가?
- [ ] **Edge Case:** (예: 허용량이 꽉 찼을 때 에러 메시지가 정상적으로 나오는가?)
    

### 2-2. 산출물 박제 (Deliverables Update)

- [ ] **API Spec:** 실제 응답값이 초기 설계와 달라졌다면 `20_Deliverables/03_API_Specification.md` 수정
- [ ] **ERD:** 필드(컬럼)가 추가되었으므로 `20_Deliverables/02_ERD.md` 업데이트

### 2-3. Troubleshooting Log
> 기술적 이슈는 `Troubleshooting/` 폴더에 별도 파일로 생성 후 여기에 링크를 거세요.
