---
type: task
status: 🟡 Doing
created: 01-03 20:07
tags:
  - task
---
> Target API 상세:

- **Endpoint:** `GET /fridge/slots`
- **Query Params:**
  - `floor` (int, optional)
  - `view` (string, optional, default=`full`, unknown → `full`)
  - `page` (int, optional, default=0)
  - `size` (int, optional, default=20, range 1~200)
- **Response JSON (FridgeSlotListResponse):**
	- items 배열 + 페이지 정보를 반환하면 되겠다.
```JSON
{
  "items": [
    {
      "slotId": "uuid",
      "slotIndex": 0,
      "slotLetter": "A",
      "floorNo": 2,
      "floorCode": "2F",
      "compartmentType": "FRIDGE",
      "resourceStatus": "ACTIVE",
      "slotStatus": "ACTIVE",
      "locked": false,
      "lockedUntil": "2024-01-01T12:00:00Z",
      "capacity": 3,
      "displayName": "2F-A-01",
      "occupiedCount": 1
    }
  ],
  "totalCount": 1,
  "page": 0,
  "size": 20,
  "totalPages": 1
}
```

---

## 1. 🥩 [Step 3] 실무 구현: 살 붙이기 (Implementation)

**목표:** Phase 1에서 만든 뼈대(Skeleton)와 계약(DTO)을 바탕으로 실제 작동하는 코드를 작성합니다.

### 1-0. 설계 흐름 요약 (요청 → 설계 확정)

> **Flow:** OpenAPI → 프론트 사용 패턴 → 정책/권한 → 확장성 → 정합성

1. **계약 확인 (OpenAPI):** `GET /fridge/slots`의 쿼리와 응답 스키마를 확정한다.
2. **프론트 호출 분석:** `view=full`, `page=0`, `size=200`이 기본 패턴임을 확인한다.
3. **정책/권한 반영:** 거주자/층별장/관리자 스코프 규칙을 적용한다.
4. **확장성 고려:** 현재 2~5층이지만 `floor`는 정수만 검증하고 존재하지 않으면 0건 반환.
5. **호환성 결정:** `view`는 알 수 없는 값도 `full`로 fallback 처리한다.
6. **정합성 규칙 정의:** `locked`, `slotStatus`, `occupiedCount` 등의 일관성 규칙을 체크리스트로 고정한다.

### 1-1. 스키마 상세화 (Schema Refinement)

> **Question:** API의 필터링(`status`), 에러 처리(`capacity`), 정렬(`createdAt`)을 위해 **Entity에 어떤 컬럼이 추가되어야 하나요?**

|**Entity**|**필드명**|**타입**|**필수여부**|**추가 사유 (Validation/Logic)**|
|---|---|---|---|---|
|`Bundle`|`status`|Enum|Y|삭제된 꾸러미를 제외하고 조회하기 위해|
|`Slot`|`capacity`|Integer|Y|물품이 꽉 찼는지(Max) 검증하기 위해|
|`Item`|`expiryDate`|LocalDate|N|D-Day 계산을 위한 원천 데이터|

### 1-2. 매핑 및 로직 설계 (Strategy)

> **Mapping:** DTO의 데이터를 Entity로 바꿀 때, 혹은 그 반대일 때의 규칙을 정합니다.

- **Request 핸들링 (Query):**
  - `floor`, `view`, `page`, `size`를 정규화한다.
  - `view`는 `full` 이외 값도 `full`로 처리한다.
  - `page/size`는 범위 클램프(0 이상, size 1~200) 적용.
- **Response 핸들링 (Entity → DTO):**
  - `Slot` → `FridgeSlotResponse`
  - `locked`는 `slotStatus`/`lockedUntil`과 정합성 유지
  - `displayName`은 서버에서 확정하여 반환

> **Business Logic:** "데이터를 저장하기 전/후에 무엇을 체크해야 하는가?"

1. **사전 검증:** `floor/page/size`의 형식과 범위를 정규화한다.
2. **핵심 로직:** 역할/스코프에 맞는 슬롯만 조회한다.
3. **후처리:** 응답 정합성(`locked`, `slotStatus`, `occupiedCount`)을 보장한다.

### 중복 필드 해석 (UI 편의 필드)

> **Note:** 계약상 중복처럼 보이는 필드는 UI 편의용 캐시 필드입니다. 삭제/통합하지 않습니다.

- `floorNo` vs `floorCode`
  - `floorNo`: 정렬/필터/비즈니스 로직용 숫자
  - `floorCode`: UI 표시 문자열(예: `"2F"`)
- `slotIndex` vs `slotLetter`
  - `slotIndex`: 내부 정렬/식별용 인덱스
  - `slotLetter`: 사용자 표기용 라벨(예: `"A"`)
- `displayName`
  - UI에서 조합하지 않도록 서버가 확정 제공하는 표기용 문자열

### 1-2. 슬롯 조회 DTO/매핑 규칙 (요약)

- **요청 DTO (Query 모델):**
  - `floor`: Integer, optional
  - `view`: String, optional (default `full`)
  - `page`: Integer, optional (default 0)
  - `size`: Integer, optional (default 20, clamp 1~200)
- **응답 DTO (FridgeSlotResponse):**
  - `slotId`: UUID
  - `slotIndex`: Integer
  - `slotLetter`: String
  - `floorNo`: Integer
  - `floorCode`: String
  - `compartmentType`: String
  - `resourceStatus`: String
  - `slotStatus`: String (`ACTIVE|LOCKED|IN_INSPECTION`)
  - `locked`: Boolean
  - `lockedUntil`: DateTime (nullable 가능)
  - `capacity`: Integer
  - `displayName`: String
  - `occupiedCount`: Integer

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

- [ ] **기본 호출:** `GET /fridge/slots` (view 없이) → `view=full` 처리되는가?
- [ ] **View Fallback:** `view=weird` → `full`로 fallback 되는가?
- [ ] **층 필터:** `floor=999` → 200 + 빈 리스트로 반환되는가?
- [ ] **페이지/사이즈:** `page=-1&size=999` → page=0, size=200으로 클램프되는가?
- [ ] **권한 범위:** 거주자/층별장/관리자 스코프가 정확히 반영되는가?
- [ ] **응답 정합성:** `locked=true`면 `lockedUntil` 존재, `IN_INSPECTION`이면 `locked=true`인가?
    

### 2-2. 산출물 박제 (Deliverables Update)

- [ ] **API Spec:** 실제 응답값이 초기 설계와 달라졌다면 `20_Deliverables/03_API_Specification.md` 수정
- [ ] **ERD:** 필드(컬럼)가 추가되었으므로 `20_Deliverables/02_ERD.md` 업데이트

### 2-3. Troubleshooting Log
> 기술적 이슈는 `Troubleshooting/` 폴더에 별도 파일로 생성 후 여기에 링크를 거세요.
