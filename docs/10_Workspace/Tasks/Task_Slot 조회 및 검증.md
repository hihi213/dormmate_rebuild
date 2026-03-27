---
type: task
status: 🟡 Doing
created: 01-03 20:07
tags:
  - task
---

## **계약 요약 (OpenAPI)**
- Method/Path: GET /fridge/slots
- Query params (all optional):
    - floor (int32)
    - view (string)
    - page (int32)
    - size (int32)
- 200 Response: FridgeSlotListResponse
    - items: `FridgeSlotResponse[]`
    - totalCount (int64)
    - page (int32)
    - size (int32)
    - totalPages (int32)
- FridgeSlotResponse fields:
    - slotId (uuid)
    - slotIndex (int32)
    - slotLetter (string)
    - floorNo (int32)
    - floorCode (string)
    - compartmentType (string)
    - resourceStatus (string)
    - slotStatus (enum: ACTIVE|LOCKED|IN_INSPECTION)
    - locked (boolean)
    - lockedUntil (date-time)
    - capacity (int32)
    - displayName (string)
    - occupiedCount (int32)
- 오류 응답: ProblemDetailResponse (400/401/403/404/409/422/500)

---

## 1. 계약 확정


다른 층 냉장고의 개별 칸 관리도 할수있어서 층단위보단 칸단위가 적합할것같다 그러면, 관리칸과 물품 배정칸이 중복일수도 있는데

- 어떤 역할이 어떤 칸을 볼 수 있는지
	- 거주자와 냉장고와 거주호실의 최소 관계정의
		- 거주자는 호실에 거주한다.
		- 호실은 층에 속해있다
		- 슬롯(냉장고칸)은 냉장고에 속해있다
		- 응답 DTO에는 냉장고가 없으므로, 내부 도메인 모델로 만들자.
	- 거주자
		- 물품 보관: 본인에게 배정된 슬롯에서만 가능
		- 본인 물품만 조회,수정,삭제 가능
		- 단 해당칸이 검사중인경우 등록.수정, 삭제 불가
	- 층별장: 기본은 거주자와 권한동일
		- 검사 권한을 배정받은 칸에서 검사 세션 시작후 진행/제출, 결과로 폐기 처리
	- 관리자: 전체 조회 + 강제 수정/삭제/일괄 삭제
- slotStatus, locked, lockedUntil 계산 규칙 및 일관성
	- IN_INSPECTION: 활성 검사 세션 존재
	    - locked = true, lockedUntil 사용
	- ACTIVE: 검사 세션 없음
	    - locked = false, lockedUntil = null
	- LOCKED: 세션과 무관한 수동 잠금(관리/정비 등) 용도
	- **정합성 체크**
		- slotStatus = IN_INSPECTION ⇒ locked = true
		- slotStatus = ACTIVE ⇒ locked = false, lockedUntil = null
		- locked = true ⇒ slotStatus ≠ ACTIVE
- 페이징 규칙(page/size)과 totalPages 계산 방식
	- page: 기본 0, 음수는 0
	- size: 기본 20, 범위 1~200 클램프
	- totalPages = ceil(totalCount / size)
- view 파라미터 의미(필터/정렬/전용 뷰 여부)
	- 현재는 굳이 설정할 필요성을 못느껴서 항상 동일결과 full를 반환하게 하고 추후 확장하자.
- occupiedCount(**점유량 표시**)의 기준
	- **활성 포장(소프트 삭제 제외)** 기준 집계
	- 검사/폐기 처리된 포장은 제외

### 요청/응답 DTO 설계안 작성
- 요청
	- floor: 존재하지 않는 층이면 **200 + 빈 리스트**
	- page: 기본 0, 음수는 0
	- size: 기본 20, 범위 1~200 클램프
	- view: **무시하고 full 처리**


## 2. 연관관계 스켈레톤

## 3. 스키마 상세화

## 4. Repository, Service, Controller 구현

1. 계약 확정: 요청/응답 DTO 설계안 작성 → OpenAPI와 일치 검토
2. 관계 스케치: Slot/Bundle/Inspection 등 최소 연관관계만 스켈레톤
3. 스키마 상세화: 상태/락/용량/점유 계산 규칙 필드 확정
4. Repository: 조회 필터/페이지 쿼리 메서드 설계
5. Service: 권한검증 → 조회 → 상태 계산/매핑
6. Controller: 쿼리 파라미터 바인딩/검증 → 응답 변환
