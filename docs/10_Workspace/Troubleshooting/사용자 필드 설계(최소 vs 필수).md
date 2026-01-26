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
