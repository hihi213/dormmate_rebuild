# 사용자 필드 설계 — 과거 검토 기록

> 면접 활용 상태: 과거 설계 메모를 보존한 문서이며, 해결·검증된 면접 사례가 아니다. 후속 작업의 판단은 해당 Task에서 진행한다.

> 기록 성격: 설계 검토. 아래 원문은 실제 구현·실험 결과를 뜻하지 않는다.
> 현재 적용 확인: 2026-09-14. 당시의 전체 관리자 요구를 1차 User 모델에 선행 적용하지 않는다.

## 현재 적용 여부

- **보존할 판단:** 표시용 응답과 저장 필드를 구분하고 집계·이력의 책임을 검토한 과정.
- **현재 기준:** [README의 확장 원칙](../../../README.md#이후-확장-원칙)과
  [점진적 ERD](../../20_Deliverables/02_ERD_%26_Schema.md)를 따른다. 관리자·벌점은 후속이며,
  아래 `lastLogin / penalties / penaltyRecords`의 “반드시 구현”은 현재 Task의 의무가 아니다.
- **후속 검토:** 필드·테이블·계산 방식은 해당 관리자 Task에서 정책·계약과 함께 결정한다.
  `display_name=name`이나 코드 합성도 아래 기록만으로 확정하지 않는다.
- **근거:** [Tech Decisions](../../20_Deliverables/04_Tech_Decisions.md)의 점진적 ERD 결정과
  [Policy Baseline](../../20_Deliverables/06_Policy_Baseline.md)의 `AUTH-018`, `ADM-002`.

## 당시 기록 — 원문 보존

## 결론
Admin 사용자 목록 요구사항 때문에 `lastLogin / penalties / penaltyRecords`는 **반드시 구현**한다.

## 결정 흐름
1. OpenAPI 스펙 확인
   - UserProfileResponse, AdminUsersResponse 기준으로 필드 확인
2. 관리자 화면 요구사항 반영
   - `lastLogin`, `penalties`, `penaltyRecords`가 실제로 노출됨
3. 구현 난이도/확장성 평가
   - 표시 전용 필드는 DTO 조합으로 해결 가능
   - 집계/이력은 테이블 분리 필요

## 최종 판단 (필드별)

### 반드시 구현 (DB 컬럼/테이블 권장)
- `last_login_at`
- `penalties` (집계 컬럼 또는 계산)
- `penalty_records` 테이블

### 유지하되 단순화
- `display_name`
  - 초기에는 `name`과 동일하게 저장
  - 추후 분리 필요 시 독립 운용

### DB 컬럼 없이 계산 가능
- `room_code`, `floor_code`
  - `floor` + `room_number`로 DTO에서 합성

## Penalty 최소 테이블 초안
- `PENALTY_RECORD`
  - `id`, `user_id`, `module`, `source`, `points`, `reason`, `issued_at`
