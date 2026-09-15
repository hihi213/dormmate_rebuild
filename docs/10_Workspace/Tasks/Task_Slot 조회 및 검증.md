---
type: task
status: 🔴 Review Required
scope: MVP
created: 2026-01-03
updated: 2026-09-14
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
  - INV-025
  - INV-026
  - INV-027
  - INV-028
  - INV-029
  - INV-030
  - AUTH-001
  - AUTH-007
  - AUTH-008
tags:
  - task
  - fridge
  - slot
---

# Task — Slot 조회 및 검증

> Canonical Task. 이전 Slot Task 두 문서의 확정 내용을 통합했다.
>
> 2026-07-26 호실 단위 Slot 배정 결정으로 기존 사용자 직접 배정 전제가
> 폐기되었다. 아래 계약과 범위는 구현 전에 `Residence → Room →
> RoomSlotAssignment → FridgeSlot` 기준으로 다시 검토해야 한다.

## 0. 재검토 합의 기록

아래는 설계 합의 기록이며 업무 API 구현·검증 완료 기록이 아니다. 2026-09-14에
기존 결정·이유·제외를 보존하고 배경과 검증 계획을 보완했다. 결정별 실제
검증 결과는 수행 후 테스트·쿼리·로그 링크로 추가한다. 전체 시나리오는
[구현 전 테스트 시나리오](#4-구현-전-테스트-시나리오)에서 관리한다.

### 첫 조회 역할을 일반 거주자로 제한

- **배경·질문:** 첫 조회에서 거주자·담당자·관리자의 범위를 모두 다룰 것인가?
- **결정:** 첫 `GET /fridge/slots` 수직 슬라이스는 인증된 일반 거주자가 현재
  거주 호실에 활성 배정된 Slot을 조회하는 흐름만 지원한다.
- **이유:** 첫 학습 슬라이스에서 호실 기반 이용 권한을 끝까지 검증하는 데
  집중한다. 냉장고 담당자 관리 범위, 관리자 전체 조회와 여러 범위의 합집합을
  동시에 구현하면 권한 규칙과 테스트 원인이 불필요하게 복잡해진다.
- **제외:** 냉장고 담당자와 관리자의 Slot 조회 범위는 `INV-028`의 후속
  Task에서 계약한다.
- **검증 계획:** 일반 거주자의 현재 호실에 배정된 Slot만 반환하는지 확인한다.

### 최소 인증 경계 포함

- **배경·질문:** 전체 로그인 구현 전에도 신뢰할 수 있는 조회 주체를 어떻게 전달할 것인가?
- **결정:** Spring Security 세션에서 인증된 사용자 식별자를 복원해 내부
  인증 주체로 전달하는 경계와 미인증 요청의 `401` 검증을 포함한다.
- **이유:** `userId` Query Parameter나 임시 신뢰 헤더를 사용하면 호출자가
  다른 사용자를 사칭할 수 있고, 나중에 폐기할 보안 계약이 생긴다.
- **제외:** 회원가입, 전체 로그인 UI 연동, 로그아웃, 세션 만료·동시 로그인
  정책은 인증 Task에서 다룬다. 로그인 API가 준비되기 전에는 백엔드 통합
  테스트로 인증 경계를 검증하며 실제 프론트 연동 완료로 표시하지 않는다.
- **검증 계획:** 인증 주체 복원과 미인증 `401`, 자격 없는 사용자의 `403`을 확인한다.

### `floor` Query Parameter 제거

- **배경·질문:** 호실 배정으로 조회 범위가 정해지는데 층을 별도로 입력받을 필요가 있는가?
- **결정:** 일반 거주자용 `GET /fridge/slots`는 현재와 향후 계약에서 `floor`
  Query Parameter를 제공하지 않는다.
- **이유:** 거주자의 조회 범위는 현재 거주 호실과 그 호실의 활성 Slot
  배정으로 이미 결정된다. 호출자가 층을 다시 지정하게 하면 같은 조건을
  중복 표현하고, 층 값이 권한 범위를 바꿀 수 있다는 잘못된 인상을 준다.
- **후속 경계:** 층별 필터가 필요한 관리자·냉장고 담당자 조회는
  `INV-028`의 별도 계약에서 검토한다.
- **한계:** 파라미터 제거 자체가 접근 통제를 보장하지 않는다. 서버의 배정 조회 조건이 필요하다.
- **검증 계획:** 다른 호실에만 배정된 Slot이 결과에 포함되지 않는지 확인한다.

### `view` Query Parameter 제거

- **배경·질문:** 지원 표현이 하나인 `view=full`을 공개 선택지로 유지할 필요가 있는가?
- **결정:** 일반 거주자용 `GET /fridge/slots`에서 `view` Query Parameter를
  제거한다.
- **이유:** 허용값이 `full` 하나뿐이어서 호출자가 선택할 수 있는 동작이 없고,
  사용되지 않는 확장 지점을 미리 계약하면 요청 검증과 프론트 호출만 복잡해진다.
- **재검토 조건:** 실제로 서로 다른 Slot 표현이나 필드 집합이 필요해지면
  구체 사용 사례를 확인한 뒤 별도 응답 계약과 함께 검토한다.
- **검증 계획:** `view` 없이 호출해 합의된 응답 필드를 반환하는지 확인한다.

## 1. 목표와 제외 범위

대상 API는 `GET /fridge/slots`다.

이번 Task에서 다음을 완료한다.

- 인증된 일반 거주자의 현재 호실에 배정된 Slot 조회
- 페이지 요청 검증
- `FridgeSlotListResponse` 계약 준수
- Slot 운영 상태와 활성 Bundle 수의 일관된 응답

다음은 구현하지 않는다.

- Slot 생성·수정
- 냉장고 담당자와 관리자의 Slot 조회
- 검사 잠금 생성·연장
- Redis
- 전체 물품·인증·검사 모델 선행 설계
- UI 구조·디자인·사용자 흐름 변경과 선택적 프론트 개선. 확정 계약의 필수 연동은 AGENTS.md를 따른다.

## 2. 계약 확인과 미결 사항

아래는 현재 Task에 필요한 계약·정책 요약이다. 기준 원문은 OpenAPI와 Policy
Baseline이며, `RETIRED` 등 명시된 미결 사항을 이 절 제목이나 합의 기록만으로 확정하지 않는다.

### 요청

| Parameter | Required | Rule |
| --- | --- | --- |
| `page` | No | 기본값 `0`, 최솟값 `0` |
| `size` | No | 기본값 `20`, 범위 `1..200` |

음수 `page`와 범위를 벗어난 `size`는 값을 조용히 보정하지 않고 `400 ProblemDetail`을 반환한다.

### 권한과 조회 범위

- 거주자: 현재 거주 호실에 활성 배정된 Slot. `RETIRED` 포함 여부는 재검토한다.
- 냉장고 담당자와 관리자의 조회 범위는 이번 Task에서 지원하지 않는다.

인증은 `AUTH-001`의 Spring Security 서버 세션을 사용한다. Controller 경계에서
인증 주체를 `CurrentActor(userId, accountAuthorities)`로 변환하고, Service는 거주 배정과
현재 호실의 Slot 배정을 DB에서 조회한다. 이 업무 범위를 세션이나
`CurrentActor`에 캐시하지 않는다.

- 세션이 없거나 만료되어 인증 주체를 복원하지 못하면 `401`
- 인증됐지만 현재 거주 자격이 없으면 `AUTH-007`에 따라 `403`
- 거주자의 현재 호실에 활성 Slot 배정이 없으면 `200` 빈 목록

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

- `totalCount`는 현재 거주 호실의 활성 Slot 배정을 적용한 결과 수다.
- `totalPages`는 필터 적용 후의 `totalCount`와 요청 `size`로 계산한다.
- 조회 결과가 없어도 `items`, `totalCount`, `page`, `size`, `totalPages`를 반환한다.

### 조회 데이터 기준

- 거주자의 현재 Slot 범위는 활성 `Residence`의 Room에 연결된 `releasedAt IS NULL`인 `RoomSlotAssignment`다.
- `occupiedCount`에는 소프트 삭제되지 않은 활성 Bundle만 포함한다.
- 동일 Room과 Slot의 활성 배정 중복을 방지할 DB 제약은 영속성 설계 단계에서 확정한다.
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
- `Residence`: 사용자의 현재 거주 호실
- `Room`: Slot 이용 배정의 기본 주체
- `RoomSlotAssignment`: 호실과 Slot의 현재 배정
- `FridgeBundle`: `occupiedCount` 계산에 필요한 최소 참조와 삭제 상태

필드와 관계는 `02_ERD_&_Schema.md`의 점진적 초안을 검토 대상으로 사용한다. 인증 자격증명, Room·Residence의 전체 생명주기와 Inspection 모델은 이번 Task 범위를 합의하며 필요한 만큼만 확정한다.

## 4. 구현 전 테스트 시나리오

- 일반 거주자의 현재 호실에 배정된 범위만 반환
- `RETIRED` Slot 포함 여부는 `INV-023`과 OpenAPI의 차이를 해소한 뒤 확정된 정책대로 검증
- 잘못된 `page`, `size`는 `400`
- 빈 결과에서도 페이지 필드 반환
- 소프트 삭제 Bundle은 `occupiedCount`에서 제외
- 인증되지 않은 요청은 `401`
- 권한 범위를 벗어난 데이터가 응답에 포함되지 않음
- 같은 `displayName`이 서로 다른 냉장고에 존재해도 `slotId` 기준으로 서로 다른 Slot으로 처리
- 정렬 결과가 반복 호출과 페이지 이동에서도 안정적임
- `totalCount`가 현재 호실의 활성 Slot 배정 결과와 일치
- Slot 수에 비례해 `occupiedCount` 조회 쿼리가 증가하지 않음

현재 거주 자격이 없는 인증 사용자의 `403`과 데이터가 없는 거주자의 `200`
구분은 `AUTH-007`에 따라 검증한다.
동일 냉장고의 활성 `displayName` 중복 방지는 Slot 생성·수정과 마이그레이션
Task에서 검증한다.

## 현재 차단 조건과 후속 결정

- 현재 확인: Slot 영속성 제약과 응답 enum, `INV-023`의 RETIRED 처리와
  OpenAPI·이 Task의 설명 차이. 범위 정리만으로 해당 정책을 확정하지 않는다.
- `AUTH-007`의 401·403·빈 목록 의미는 확정됐으며 이전 미결 문구를 대신한다.
- 후속 결정: 관리 Slot 조회, 이름 변경 운영, 방 이동·재배분, 검사 잠금.
  관련 모델의 현재 조회에 필요한 제약만 검토하며 후속 기능 전체를 선행 설계하지 않는다.

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
- `INV-001`, `INV-009`와 일반 거주자의 호실 기반 조회 범위를 검증한다.
- Spring Security 세션 인증과 `CurrentActor` 변환을 통합 검증한다.
- `401`, `403`과 권한 범위 내 빈 목록을 각각 검증한다.
- 구현 후 필요한 경우에만 ERD의 확정 범위를 확장한다.

## 7. 관련 기록

- [폴더 구조는 어떻게 할까](../Troubleshooting/폴더구조는%20어떻게%20할까.md)
- [사용자 필드 설계 — 과거 검토, 현재 적용 범위 별도](../Troubleshooting/사용자%20필드%20설계(최소%20vs%20필수).md)
