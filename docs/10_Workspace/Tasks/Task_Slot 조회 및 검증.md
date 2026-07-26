---
type: task
status: 🟡 Doing
scope: MVP
created: 2026-01-03
updated: 2026-07-26
supersedes:
  - Task_Slot 조회 및 검증_재구조화
policy:
  - INV-001
  - INV-009
  - INV-010
  - INV-011
  - INV-012
  - INV-019
  - INV-020
  - INV-021
  - INV-023
  - AUTH-001
  - AUTH-006
  - AUTH-007
  - AUTH-008
  - AUTH-009
tags:
  - task
  - fridge
  - slot
---

# Task — Slot 조회 및 검증

> Canonical Task. 이전 Slot Task 두 문서의 확정 내용을 통합했다.

## 1. 목표와 제외 범위

대상 API는 `GET /fridge/slots`다.

이번 Task에서 다음을 완료한다.

- 인증 사용자의 역할과 배정 범위에 맞는 Slot 조회
- 선택적 층 필터와 페이지 요청 검증
- `FridgeSlotListResponse` 계약 준수
- Slot 운영 상태와 활성 Bundle 수의 일관된 응답

다음은 구현하지 않는다.

- Slot 생성·수정
- 검사 잠금 생성·연장
- Redis
- 전체 물품·인증·검사 모델 선행 설계
- 사용자 요청 없는 프론트 수정

## 2. 확정 계약

### 요청

| Parameter | Required | Rule |
| --- | --- | --- |
| `floor` | No | 1 이상의 정수. 존재하지 않는 층은 `200`과 빈 목록 |
| `view` | No | 기본값 `full`, 허용값은 `full` |
| `page` | No | 기본값 `0`, 최솟값 `0` |
| `size` | No | 기본값 `20`, 범위 `1..200` |

잘못된 `view`, 음수 `page`, 범위를 벗어난 `size`는 값을 조용히 보정하지 않고 `400 ProblemDetail`을 반환한다.

### 권한과 조회 범위

- 거주자: 자신에게 현재 배정된 Slot. `RETIRED`를 포함해 운영 상태로 숨기지 않는다.
- 냉장고 담당자: 자신에게 활성 관리 배정된 Slot
- 관리자: 전체 Slot
- `floor`는 각 역할의 조회 범위를 넓히지 않고 기존 범위 안에서만 필터링한다.

인증은 `AUTH-001`의 Spring Security 서버 세션을 사용한다. Controller 경계에서
인증 주체를 `CurrentActor(userId, accountAuthorities)`로 변환하고, Service는 거주 배정과
냉장고 담당자의 관리 Slot을 DB에서 조회한다. 관리 Slot 목록을 세션이나 `CurrentActor`에
캐시하지 않는다.

- 세션이 없거나 만료되어 인증 주체를 복원하지 못하면 `401`
- 인증됐지만 거주자·냉장고 담당자·관리자 중 지원되는 업무 자격이 없으면 `403`
- 거주자의 현재 Slot 배정이 없거나 냉장고 담당자의 관리 범위에 Slot이 없으면 `200` 빈 목록
- 권한 밖의 `floor` 필터도 조회 범위를 넓히지 않고 `200` 빈 목록

### 응답

- `items`, `totalCount`, `page`, `size`, `totalPages`는 항상 반환한다.
- 각 item은 OpenAPI의 `FridgeSlotResponse` 필수 필드를 반환한다.
- `displayName`은 관리자가 지정한 필수 표시값이다.
- `occupiedCount`는 소프트 삭제되지 않은 활성 Bundle 수다.
- `occupiedCount`는 음수가 될 수 없고 `capacity`를 초과한 상태는 데이터 무결성 오류로 다룬다.
- 검사 잠금 필드는 Phase 1 응답에 포함하지 않고 검사 수직 슬라이스에서 계약한다.

`size=200`은 Slot 개수가 제한적인 현재 UI 호출을 지원한다. 이 값을 다른 목록 API의 공통 정책으로 일반화하지 않는다.

### 식별자와 정렬

- Slot의 영속 식별자는 `slotId`다.
- Slot은 `fridgeId`로 소속 냉장고와 연결한다.
- 사용자 구분에는 관리자가 지정한 `displayName`을 사용한다.
- `slotIndex`, `position`, `displayOrder`, `slotLetter`는 사용하지 않는다.
- 기본 정렬은 `floorNo ASC → fridgeId ASC → displayName ASC → slotId ASC`로 고정한다.
- 마지막 `slotId` 정렬로 반복 호출과 페이지 이동에서 순서를 안정적으로 유지한다.

### 조회와 페이지 집계

- 권한 범위를 먼저 제한한 뒤 `floor` 필터를 적용한다.
- `floor` 파라미터는 사용자의 조회 권한을 넓히지 않는다.
- `totalCount`는 권한 범위와 `floor` 필터를 모두 적용한 결과 수다.
- `totalPages`는 필터 적용 후의 `totalCount`와 요청 `size`로 계산한다.
- 조회 결과가 없어도 `items`, `totalCount`, `page`, `size`, `totalPages`를 반환한다.

### 조회 데이터 기준

- 현재 Slot 배정은 `releasedAt IS NULL`인 `FridgeSlotAssignment`다.
- `occupiedCount`에는 소프트 삭제되지 않은 활성 Bundle만 포함한다.
- 동일 사용자와 Slot의 활성 배정 중복을 방지할 DB 제약은 영속성 설계 단계에서 확정한다.
- Bundle 라벨은 `INV-013`에 따라 `(slotId, labelNumber)` 조합으로 식별한다.
- `bundleId`, `labelNumber`, `labelDisplay`는 `INV-016`~`INV-018`의 공식 용어와 책임을 따른다.
- 활성 라벨 중복 방지와 삭제 후 재사용 제약은 `INV-014`에 따라 Bundle 생성 Task에서 확정한다.

### 조회 성능

- Slot마다 `occupiedCount`를 개별 조회하는 N+1 방식을 사용하지 않는다.
- 집계 쿼리, 서브쿼리 또는 일괄 집계 중 현재 범위에서 가장 단순한 방법을 선택한다.
- 목록 조회와 `totalCount` 쿼리에 동일한 권한·필터 조건을 적용한다.
- 인덱스는 조회 쿼리를 확정한 뒤 조회 조건과 실행 계획을 근거로 결정한다.

## 3. 점진적 모델 범위

이번 Task에서 필요한 모델만 설계한다.

- `User`: 조회 주체 식별에 필요한 참조
- `Fridge`: Slot 소속과 층 조회에 필요한 최소 참조
- `FridgeSlot`: Slot 메타데이터와 상태
- `FridgeSlotAssignment`: 사용자와 Slot의 현재 배정
- `SlotManagerAssignment`: 냉장고 담당자의 현재 Slot 관리 범위 조회
- `FridgeBundle`: `occupiedCount` 계산에 필요한 최소 참조와 삭제 상태

필드와 관계는 `02_ERD_&_Schema.md`의 현재 확정 범위를 따른다. 인증 자격증명, Room 전체 모델과 Inspection 모델은 이번 Task에서 확정하지 않는다.

## 4. 구현 전 테스트 시나리오

- 역할별 허용 범위만 반환
- 거주자에게 현재 배정된 `RETIRED` Slot과 상태가 함께 반환됨
- `floor`가 권한 범위를 넓히지 않음
- 존재하지 않는 양수 층은 빈 페이지
- 잘못된 `view`, `page`, `size`는 `400`
- 빈 결과에서도 페이지 필드 반환
- 소프트 삭제 Bundle은 `occupiedCount`에서 제외
- 인증되지 않은 요청은 `401`
- 권한 범위를 벗어난 데이터가 응답에 포함되지 않음
- 같은 `displayName`이 서로 다른 냉장고에 존재해도 `slotId` 기준으로 서로 다른 Slot으로 처리
- 정렬 결과가 반복 호출과 페이지 이동에서도 안정적임
- `totalCount`가 권한과 `floor` 필터 적용 결과와 일치
- Slot 수에 비례해 `occupiedCount` 조회 쿼리가 증가하지 않음

지원되지 않는 역할의 `403`과 데이터가 없는 `200`의 구분은 `AUTH-007`을 따른다.
동일 냉장고의 활성 `displayName` 중복 방지는 Slot 생성·수정과 마이그레이션
Task에서 검증한다.

## 5. 구현 순서

- [ ] OpenAPI DTO를 기준으로 Request·Response 작성
- [ ] 최소 모델과 DB 제약 검토
- [ ] 내부 인증 주체를 입력으로 한 역할별 조회 경계 설계
- [ ] Repository 조회와 활성 Bundle 집계 방식 결정
- [ ] Service 권한 범위·필터·응답 조립
- [ ] Controller와 Bean Validation
- [ ] 단위·통합 테스트
- [ ] API 구현 현황과 관련 문서 갱신

## 6. 완료 조건

- 테스트용 DB를 포함한 관련 테스트가 통과한다.
- 요청·응답과 `ProblemDetail`이 OpenAPI와 일치한다.
- `INV-001`과 역할별 조회 범위를 검증한다.
- Spring Security 세션 인증과 `CurrentActor` 변환을 통합 검증한다.
- `401`, `403`과 권한 범위 내 빈 목록을 각각 검증한다.
- 구현 후 필요한 경우에만 ERD의 확정 범위를 확장한다.

## 7. 관련 기록

- [폴더 구조는 어떻게 할까](../Troubleshooting/폴더구조는%20어떻게%20할까.md)
- [사용자 필드 설계](../Troubleshooting/사용자%20필드%20설계(최소%20vs%20필수).md)
