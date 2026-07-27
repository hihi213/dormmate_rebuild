# Policy Baseline

> DormMate Rebuild에서 현재 적용할 업무 정책과 검토 상태를 관리한다.
>
> 원본 요구사항과 아이디어는 `docs/00_Blueprint/Feature_Inventory.md`에 보존하고,
> 구현 시 적용할 확정 정책은 이 문서를 기준으로 판단한다.

## 1. 문서 역할

이 문서는 권한, 소유권, 상태 전이, 삭제·보존, 업무 검증 규칙의 SSOT다.

- `Feature_Inventory.md`: 기존 프로토타입의 기능, 정책 후보, 확장 아이디어를 보존하는 원본 자료
- `06_Policy_Baseline.md`: Rebuild에 적용하기로 합의한 현재 정책과 미결 정책
- `03_API_Specification.md`: 공개 API의 요청·응답 계약
- `04_Tech_Decisions.md`: 정책을 구현하기 위한 중요한 기술 결정과 근거
- `API_Implementation_Status.md`: API별 범위와 구현·검증 진행 상태

프론트엔드의 현재 동작과 UI 흐름은 정책을 발견하는 근거로 사용할 수 있지만,
그 자체만으로 백엔드 정책을 확정하지 않는다.

## 2. 정책 검토 근거

새 기능 또는 수직 슬라이스의 정책을 검토할 때 다음 근거를 함께 확인한다.

1. Feature Inventory의 명시적 요구사항과 업무 사실
2. 현재 UI·UX와 프론트 호출 흐름
3. OpenAPI의 기존 계약
4. 현재 코드와 데이터 모델
5. 테스트와 실제 동작
6. Rebuild의 MVP 범위와 학습 목표

Feature Inventory에 명시된 내용은 우선적인 1차 검토 근거로 사용한다.
다른 근거와 충돌하지 않고 Rebuild에도 적용하기로 합의한 경우 이 문서에
`Confirmed`로 기록한다.

근거는 다음과 같이 구분한다.

- 명시된 업무 사실: 원본 문서에서 직접 확인할 수 있는 요구사항
- 해석된 정책: 여러 업무 사실을 현재 Rebuild에 적용하도록 합의한 규칙
- 추론 또는 미결 정책: 문서에 직접 명시되지 않았거나 근거가 충돌하는 규칙
- 기술 결정: 업무 정책을 구현하기 위한 DB 제약, 인덱스, 락 또는 인프라 선택

문맥에서 추론한 내용은 사용자 확인 전까지 `Review Required`로 기록한다.
DB 제약과 기술 구현 방식은 업무 정책과 구분해 ERD 또는 Tech Decisions에서 결정한다.

## 3. 정책 상태

| Status | 의미 | 구현 가능 여부 |
| --- | --- | --- |
| `Confirmed` | Rebuild에 적용하기로 합의한 정책 | 구현 가능 |
| `Review Required` | 문서·코드가 충돌하거나 추가 결정이 필요한 정책 | 결정 전 구현 금지 |
| `Post-MVP` | 유효한 정책 후보지만 Release 1 범위 밖 | MVP에서는 구현하지 않음 |
| `Legacy` | 원본에는 있으나 Rebuild에서는 적용하지 않기로 한 정책 | 구현하지 않음 |

정책 상태와 API 구현 상태는 서로 다르다. 이 문서에는 `In Progress`,
`Implemented` 같은 진행 상태를 기록하지 않는다.

## 4. 변경 절차

1. Feature Inventory, UI 흐름, 프론트 코드 또는 기존 계약에서 정책 후보를 찾는다.
2. 이 문서에 `Review Required`로 기록하고 충돌과 선택지를 정리한다.
3. 사용자와 합의한 뒤 `Confirmed`, `Post-MVP` 또는 `Legacy`로 변경한다.
4. 공개 인터페이스가 달라지면 OpenAPI를 먼저 갱신한다.
5. 중요한 기술 선택이 수반되면 Tech Decisions에 근거를 기록한다.
6. 관련 Task와 API 구현 현황을 갱신한 뒤 구현한다.

확정 정책을 바꿀 때는 기존 행을 조용히 덮어쓰지 않는다. Notes에 변경 이유와
관련 결정 문서를 남기거나, 의미가 크게 달라지면 새 정책 ID를 추가한다.

## 5. 현재 정책

### 5.1 프로젝트와 프론트엔드 경계

| ID | Scope | Status | Policy | Evidence / Notes |
| --- | --- | --- | --- | --- |
| PRJ-001 | MVP | `Confirmed` | 기존 프론트엔드의 UI와 UX를 유지한다. | 사용자 합의 |
| PRJ-002 | MVP | `Confirmed` | 프론트 API Client, 타입, 인증 전달과 요청·응답 매핑은 사용자가 수정을 요청한 경우 Codex가 변경한다. | 사용자 합의 |
| PRJ-003 | MVP | `Confirmed` | 프론트 어댑터로 백엔드 계약의 의미 불일치를 숨기지 않는다. | 계약 우선 협업 원칙 |
| PRJ-004 | MVP | `Confirmed` | UI 구조나 사용자 흐름 변경은 사용자와 먼저 합의한다. | 사용자 합의 |
| PRJ-005 | MVP | `Confirmed` | Fixture는 개발 환경에서만 허용하며 배포 환경에서 실제 구현처럼 노출하지 않는다. | 포트폴리오 검증 원칙 |

### 5.2 물품 관리

#### 공식 용어

| 용어 | 코드 | 정의 |
| --- | --- | --- |
| 포장 ID | `bundleId` | Bundle의 UUID 영속 식별자. API 조회·수정·삭제와 영구 이력 연결에 사용한다. |
| 라벨 번호 | `labelNumber` | 동일 Slot 안에서 발급하는 `1..999` 범위의 사용자용 숫자. 화면에서는 3자리로 표시한다. |
| 표시 라벨 | `labelDisplay` | Slot의 사용자 표시값과 `labelNumber`를 조합하는 응답용 계산 문자열 후보다. 최종 형식과 제공 주체는 `INV-017`에서 검토한다. |
| 물품 ID | `itemId` | Item의 UUID 영속 식별자다. |

Feature Inventory의 `스티커 번호`는 Rebuild 계약의 `라벨 번호(labelNumber)`에 대응한다.
`포장 번호`는 `bundleId`와 `labelNumber`를 혼동할 수 있으므로 공식 계약과 Rebuild 문서에서 사용하지 않는다.

`labelDisplay`를 사용하는 경우에도 Bundle 테이블의 영속 식별자나 유일성 기준으로
사용하거나 중복 저장하지 않는다. `slotLetter` 제거 결정에 따라 `slotLabel`,
`labelDisplay`의 최종 형식과 서버·프론트 중 계산 주체는 다시 확정해야 한다.

#### 냉장고와 칸

- 냉장고(`Fridge`)는 특정 층에 설치된 물리적 설비이며 여러 Slot을 소유한다.
- 하나의 냉장고는 냉장 Slot과 냉동 Slot을 동시에 소유할 수 있다.
- Slot은 `slotId`로 영속 식별하고 `fridgeId`로 소속 냉장고와 연결한다.
- 사용자는 관리자가 지정한 필수 `displayName`으로 Slot을 구분한다.
- `"A칸"`, `"1번 칸"`, `"상단 냉장칸"` 같은 표기는 모두 `displayName` 값으로 표현한다. 알파벳 표시는 폐기한 것이 아니라 별도 `slotLetter` 필드와 `slotIndex` 기반 계산 규칙을 제거하고 `displayName`으로 일반화한 것이다.
- 동일 냉장고의 활성 Slot은 중복된 `displayName`을 사용할 수 없다.
- 표시 순서 요구가 확정되기 전까지 `slotIndex`, `position`, `displayOrder`와 `slotLetter`를 도입하지 않는다.
- `displayName`은 사용자 표시값이며 API 관계, 배정, 포장과 검사 연결에는 `slotId`를 사용한다.

| ID | Scope | Status | Policy | Evidence / Notes |
| --- | --- | --- | --- | --- |
| INV-001 | MVP | `Confirmed` | 거주자는 현재 거주 호실에 배정된 냉장고 데이터에 접근한다. 관리자와 냉장고 담당자의 범위는 별도로 정의한다. | Feature Inventory의 배정 칸 제한과 2026-07-26 호실 단위 배정 합의 |
| INV-002 | MVP | `Confirmed` | 포장과 물품의 수정·삭제에는 소유권 검증이 필요하다. | Feature Inventory와 실제 UI 흐름 |
| INV-003 | MVP | `Confirmed` | 포장 메모는 작성자만 열람할 수 있다. 관리자의 열람 여부는 별도 정책으로 확정한다. | Feature Inventory |
| INV-004 | MVP | `Confirmed` | 검사로 잠긴 대상은 일반 사용자가 수정하거나 삭제할 수 없다. | Feature Inventory와 MVP 합의 |
| INV-005 | MVP | `Confirmed` | 포장과 물품의 일반 삭제는 소프트 삭제로 처리한다. | Feature Inventory와 MVP 합의 |
| INV-006 | MVP | `Confirmed` | 현재 UI의 포장 등록은 물품을 최소 1개 요구한다. 백엔드도 빈 포장 생성을 허용하지 않는다. | 실제 프론트 폼 검증과 사용자 흐름 |
| INV-007 | MVP | `Review Required` | 목록 검색·필터·통계를 서버에서 수행할 범위와 페이지 크기를 확정해야 한다. | 현재 프론트는 최대 200건을 받아 일부 처리를 클라이언트에서 수행 |
| INV-008 | MVP | `Review Required` | 삭제 데이터의 보존 기간과 관리자 강제 삭제가 논리 삭제인지 물리 삭제인지 확정해야 한다. | 원본 문서와 MVP 관리자 범위의 세부 의미 미확정 |
| INV-009 | MVP | `Confirmed` | 일반 거주자의 Slot 조회 범위는 현재 거주 호실에 활성 배정된 Slot이다. 첫 `GET /fridge/slots` Task는 이 일반 거주자 범위만 지원한다. | 호실 단위 배정과 첫 수직 슬라이스 범위를 2026-07-27 사용자 확인 |
| INV-010 | MVP | `Confirmed` | Slot 조회의 음수 `page`와 범위를 벗어난 `size`는 fallback이나 clamp 없이 `400`으로 거부한다. | 명시적 입력 검증으로 확정 |
| INV-011 | MVP | `Confirmed` | Slot의 `occupiedCount`는 소프트 삭제되지 않은 활성 포장 수다. | Slot Task 확정. `displayName` 정책은 `INV-019`로 분리했다. |
| INV-012 | MVP | `Confirmed` | Slot의 영속 식별자는 `slotId`이며 배정, 포장과 검사 관계는 이 ID를 사용한다. | Feature Inventory의 칸 ID 저장 정책. 2026-07-26 합의로 표시·정렬용 `slotIndex` 전제를 제거했다. |
| INV-013 | MVP | `Confirmed` | 포장 라벨 번호는 Slot별로 발급하며 포장 라벨은 `(slotId, labelNumber)` 조합으로 식별한다. | Feature Inventory의 “칸 ID와 3자리 숫자 저장” 및 “칸별 라벨 시퀀스” |
| INV-014 | MVP | `Review Required` | 동일 Slot에서 활성 포장의 `labelNumber` 중복을 방지할 DB 제약과 삭제 후 재사용 시점을 확정해야 한다. | 라벨 재사용 정책은 있으나 재사용 시점과 이력 충돌 처리 미정 |
| INV-015 | MVP | `Legacy` | `slotIndex`의 유일성 범위를 정하지 않는다. 현재 모델에는 `slotIndex`를 도입하지 않는다. | 2026-07-26 합의: 칸 표시는 `displayName`, 영속 식별은 `slotId`가 담당한다. |
| INV-016 | MVP | `Confirmed` | `bundleId`와 `labelNumber`를 구분하며 모호한 `포장 번호` 용어를 공식 계약에서 사용하지 않는다. | 식별자와 사용자용 라벨 분리 |
| INV-017 | MVP | `Review Required` | Bundle의 사용자 표시에는 영속 원천 데이터만 사용하고 계산 결과를 DB 식별자·검색 관계 키·유일성 기준으로 사용하지 않는다. `slotLabel`과 `labelDisplay`를 계약에서 제거할지, `displayName + labelNumber` 계산값으로 제공할지 확정해야 한다. | `slotIndex`·`slotLetter` 제거 결정으로 기존 `A001` 형식 재검토 필요 |
| INV-018 | Post-MVP | `Review Required` | 검사·감사·알림 등 과거 사건에서 당시 표시 라벨을 보존할 스냅샷 필드와 범위를 확정해야 한다. | Slot 표시 변경·라벨 재사용 시 과거 표시 혼동 가능 |
| INV-019 | MVP | `Confirmed` | Slot의 `displayName`은 관리자가 지정하는 필수 사용자 표시값이다. 동일 냉장고의 활성 Slot은 중복된 `displayName`을 사용할 수 없다. | 2026-07-26 사용자 합의 |
| INV-020 | MVP | `Confirmed` | Slot은 `fridgeId`로 하나의 물리적 냉장고에 소속되며, 하나의 냉장고는 냉장·냉동 Slot을 함께 소유할 수 있다. | 2026-07-26 사용자 합의 |
| INV-021 | MVP | `Confirmed` | 명시적인 표시 순서 요구가 생기기 전까지 `slotIndex`, `position`, `displayOrder`와 파생 `slotLetter`를 모델·DB·공개 Slot 계약에 두지 않는다. | 불필요한 순서·표시 데이터 제거 합의 |
| INV-022 | MVP | `Review Required` | `displayName` 변경이 기존 실물 표기와 검사·감사 이력에 미치는 영향, 이름 정규화와 소프트 삭제 후 재사용 기준을 확정해야 한다. | 표시명 변경 운영 정책 미정 |
| INV-023 | MVP | `Review Required` | 거주자의 Slot 조회는 현재 거주 관계와 현재 호실의 활성 Slot 배정을 기준으로 한다. `RETIRED` Slot의 조회·신규 등록 제한과 기존 호실 배정 해제 절차는 Slot 운영 상태 수직 슬라이스에서 다시 확정한다. | 호실 단위 배정 전환으로 기존 사용자 직접 배정의 생명주기 규칙 재검토 필요 |
| INV-024 | MVP | `Confirmed` | Phase 1 Slot 조회에는 검사 잠금 상태를 공개하지 않는다. `slotStatus`, `locked`, `lockedUntil`은 검사 잠금의 원천·만료·상태 전이를 확정한 뒤 검사 수직 슬라이스에서 계약한다. | 미구현 검사 기능을 고정값으로 노출하지 않기 위한 MVP 경계 |
| INV-025 | MVP | `Confirmed` | 냉장고 Slot의 기본 이용 배정 주체는 사용자가 아니라 호실이다. 거주자는 현재 유효한 거주 관계를 통해 현재 호실에 활성 배정된 냉장·냉동 Slot을 이용한다. 사용자별 예외 Slot 배정은 실제 요구가 확인되기 전까지 도입하지 않는다. | 호실 구간을 공통 칸에 배분하는 운영 방식과 2026-07-26 사용자 재확인 |
| INV-026 | MVP | `Confirmed` | 기본 냉장 3칸·냉동 1칸은 물리적 냉장고 한 대의 고정 구성이 아니라 층별 호실을 Slot에 배분할 때 사용하는 현재 운영 정책이다. 같은 층의 호실을 순서대로 균등 구간화하며, 24개 호실이면 냉장 Slot 하나당 8개 호실, 냉동 Slot 하나는 24개 호실이 공동 사용한다. 나누어떨어지지 않는 경우와 관리자 재배분 규칙은 별도 검토한다. | Feature Inventory 배분 정책의 의미를 2026-07-26 사용자 설명으로 명확화 |
| INV-027 | MVP | `Confirmed` | 방 이동으로 새 호실의 Slot 이용 범위가 적용되기 전에, 사용자가 이전 호실을 통해서만 접근할 수 있던 Slot의 기존 포장을 정리해야 한다. 구체적인 정리 확인·이동 처리와 실패 응답은 거주 이동 또는 Bundle 수직 슬라이스에서 계약한다. | 방 이동 직후 기존 물품이 접근 불가능해지는 문제를 방지하기 위한 2026-07-26 합의 |
| INV-028 | MVP | `Review Required` | 냉장고 담당자의 관리 Slot과 관리자의 전체 Slot을 일반 거주자용 `GET /fridge/slots`에 함께 반환할지, 역할별 별도 조회 계약을 둘지는 후속 Task에서 확정한다. | 첫 Slot Task에서 다중 역할과 결과 합집합을 제외한 2026-07-27 범위 결정 |
| INV-029 | MVP | `Confirmed` | 일반 거주자용 `GET /fridge/slots`는 `floor` Query Parameter를 제공하지 않는다. 조회 층은 현재 거주 호실과 그 호실의 Slot 배정으로 이미 제한된다. 층별 운영 조회가 필요하면 관리자·냉장고 담당자용 계약에서 별도로 검토한다. | 중복 입력과 권한 범위 혼동을 제거한 2026-07-27 사용자 확인 |
| INV-030 | MVP | `Confirmed` | 일반 거주자용 `GET /fridge/slots`는 `view` Query Parameter를 제공하지 않는다. 지원 표현이 하나뿐인 `view=full`은 선택 의미가 없으며, 다른 조회 형태가 필요하면 구체 요구와 응답 계약을 별도로 검토한다. | 사용되지 않는 확장 파라미터를 제거한 2026-07-27 사용자 확인 |

### 5.3 거주 구역과 공용시설

층별 성별 구성과 시설 이용 대상은 현재 Rebuild의 업무 정책으로 관리한다.
성별·층·호실을 어떤 컬럼과 관계로 저장할지는 관련 수직 슬라이스에서 별도로
결정하며, 아래 정책만으로 User 필드나 권한 계산 방식을 확정하지 않는다.

| ID | Scope | Status | Policy | Evidence / Notes |
| --- | --- | --- | --- | --- |
| RES-001 | MVP | `Confirmed` | 하나의 거주 층에는 남성과 여성이 함께 거주하지 않는다. 각 거주 층은 한 성별의 생활 구역으로 운영한다. | 2026-07-26 사용자 확인 |
| RES-002 | MVP | `Confirmed` | 현재 운영 기준은 2층 남성, 3·4·5층 여성 거주다. 운영 기준이 실제로 변경되면 코드 상수에 의존하지 않고 이 정책과 관련 설정·데이터를 함께 갱신한다. | Feature Inventory와 2026-07-26 사용자 재확인 |
| FAC-001 | MVP | `Confirmed` | 다목적실과 도서관은 모든 활성 거주자가 이용할 수 있다. 관리자 등 비거주 계정의 일반 이용 권한은 별도로 정의한다. | 2026-07-26 사용자 확인 |
| LAUN-001 | MVP | `Confirmed` | 일반 세탁실은 남성용과 여성용으로 구분하며, 활성 거주자는 자신의 성별에 허용된 세탁실과 그 안의 세탁기·건조기를 이용할 수 있다. 성별과 현재 거주 층 중 무엇을 권한 판정의 원천으로 저장할지는 세탁실 수직 슬라이스에서 결정한다. | 2026-07-26 사용자 확인 |
| LAUN-002 | MVP | `Confirmed` | 공용 건조기는 성별 구분 없이 모든 활성 거주자가 이용할 수 있다. | 2026-07-26 사용자 확인 |
| FAC-002 | MVP | `Confirmed` | 냉장고는 공용시설과 달리 현재 호실에 배정된 Slot만 이용한다. 구체 배정과 방 이동 정책은 `INV-025`~`INV-027`을 따른다. | 2026-07-26 사용자 확인과 냉장고 배정 결정 |

### 5.4 인증과 사용자

| ID | Scope | Status | Policy | Evidence / Notes |
| --- | --- | --- | --- | --- |
| AUTH-001 | MVP | `Confirmed` | Spring Security 기반 서버 세션 인증만 사용한다. 브라우저는 `HttpOnly`, `SameSite=Lax` 세션 쿠키를 전달하고 배포 HTTPS 환경에서는 `Secure`를 적용한다. Bearer Access Token, Refresh Token과 `deviceId` 기반 인증은 MVP에서 사용하지 않고 Post-MVP 후보로 분리한다. | 2026-07-26 서버 세션 선택, 2026-07-27 토큰 인증을 후속 범위로 분리하기로 사용자 합의. 처음부터 세션 인증 경계를 학습하고 MVP의 이중 인증 복잡성을 피한다. |
| AUTH-002 | MVP | `Review Required` | 온라인 회원가입을 제공할지, 관리자가 계정을 발급할지 확정해야 한다. | Feature Inventory와 현재 비활성 회원가입 UI가 충돌 |
| AUTH-003 | MVP | `Confirmed` | 비활성화된 사용자는 로그인할 수 없다. | Feature Inventory |
| AUTH-004 | MVP | `Confirmed` | 관리자 계정 권한, 거주 자격과 냉장고 관리 업무를 분리하고 각 API에서 서버가 검증한다. 냉장고 담당자는 거주자에게 추가되는 기간성 업무다. | Feature Inventory의 층별장 역할을 Rebuild 업무 배정으로 재설계 |
| AUTH-005 | MVP | `Review Required` | 로그아웃은 세션 무효화로 처리한다. idle timeout, 동시 로그인 제한과 다중 기기 정책의 세부값은 인증 구현 Task에서 확정한다. Refresh Token API는 MVP 공개 계약에서 제외하고 Post-MVP 토큰 인증 검토 때 별도 계약한다. | 세션 인증 선택과 2026-07-27 토큰 인증 후속 범위 분리에 따른 재분류 |
| AUTH-011 | Post-MVP | `Review Required` | Bearer Access Token, Refresh Token과 `deviceId` 기반 인증은 향후 지원 후보로 둔다. 지원 필요성, 세션과의 병행 여부, 토큰 저장·회전·폐기와 기기 식별 목적을 다시 검토한 뒤 별도 계약으로 확정한다. | 2026-07-27 사용자 합의. “다음에 지원”은 현재 OpenAPI의 토큰 계약을 자동 확정하거나 MVP에서 구현한다는 의미가 아니다. |
| AUTH-012 | MVP | `Confirmed` | 로그인 성공은 한 번의 `POST /auth/login` 호출에서 서버 세션을 생성하고 `200 OK`로 `UserProfileResponse`를 반환한다. 세션 식별자는 응답 본문에 넣지 않고 `HttpOnly` 쿠키로만 전달하며 Access/Refresh Token은 반환하지 않는다. | 2026-07-27 사용자 합의. 로그인 직후 별도 프로필 조회 없이 화면 상태를 구성하면서 토큰 노출과 이중 인증을 피한다. |
| AUTH-013 | MVP | `Confirmed` | 로그인 화면 진입 시 `GET /csrf`로 토큰을 미리 발급받아 프론트 메모리에만 보관한다. 로그인 제출은 준비된 토큰을 서버가 응답한 헤더 이름으로 전달하여 `POST /auth/login` 한 번만 호출한다. 토큰 준비 전에는 제출하지 않으며 브라우저 영속 저장소에 보관하지 않는다. | 2026-07-27 사용자 합의. 로그인 버튼 동작을 단일 API 호출로 유지하면서 로그인 요청도 CSRF로 보호한다. |
| AUTH-014 | MVP | `Confirmed` | 존재하지 않는 아이디와 잘못된 비밀번호는 외부 응답에서 구분하지 않고 모두 `401 Unauthorized`, 오류 코드 `AUTH_INVALID_CREDENTIALS`, 안내 문구 `아이디 또는 비밀번호가 올바르지 않습니다.`를 반환한다. 실제 실패 원인은 내부 보안 로그에서만 구분할 수 있다. | 2026-07-27 사용자 합의. 계정 존재 여부 추측을 막고 프론트 오류 처리를 안정화한다. |
| AUTH-015 | MVP | `Confirmed` | 자격 증명이 올바르지만 계정이 비활성 상태이면 `403 Forbidden`, 오류 코드 `ACCOUNT_INACTIVE`, 안내 문구 `비활성화된 계정입니다. 관리자에게 문의해 주세요.`를 반환한다. 자격 증명이 틀리면 비활성 여부를 밝히지 않고 `AUTH-014`를 적용하며, 비활성 사유와 내부 관리자 메모는 응답에 노출하지 않는다. | 2026-07-27 사용자 합의. 관리자 발급형 기숙사 서비스에서 사용자가 비밀번호 오류로 오해하지 않고 운영자에게 문의할 수 있게 한다. |
| AUTH-016 | MVP | `Confirmed` | CSRF 토큰이 누락·불일치·만료된 요청은 원인을 외부에서 세분화하지 않고 `403 Forbidden`, 오류 코드 `CSRF_INVALID`, 안내 문구 `보안 토큰이 유효하지 않습니다. 페이지를 새로고침한 뒤 다시 시도해 주세요.`를 반환한다. 프론트는 기존 토큰을 폐기하고 새 토큰 준비가 끝날 때까지 재제출을 막는다. | 2026-07-27 사용자 합의. 일반 권한 부족과 CSRF 실패를 안정적으로 구분하면서 검증 세부정보는 노출하지 않는다. |
| AUTH-017 | MVP | `Confirmed` | 로그아웃은 요청 본문 없는 `POST /auth/logout`으로 현재 서버 세션을 무효화하고 세션 쿠키를 만료시킨다. 성공은 `204 No Content`이며 이미 세션이 없거나 만료됐어도 `204`를 반환한다. CSRF 검증 실패만 `AUTH-016`에 따라 `403 CSRF_INVALID`로 반환한다. | 2026-07-27 사용자 합의. refresh token 없이 세션 로그아웃을 처리하고 반복 요청의 최종 상태를 동일하게 유지한다. |
| AUTH-006 | MVP | `Confirmed` | 냉장고 관리 권한은 `SlotManagerAssignment`로 Slot 단위 저장한다. 냉장고 담당자 지정 대상은 활성 거주자여야 하고 기존 거주자 권한을 유지한다. 거주 층과 관리 Slot의 층은 영구적인 DB 제약으로 묶지 않는다. | 일부 Slot·다른 층 담당 확장과 명시적 권한 이력을 위한 2026-07-26 사용자 합의 |
| AUTH-009 | MVP | `Confirmed` | 관리자 UI는 층 전체와 개별 Slot 선택을 제공한다. 층 전체 선택은 선택 시점의 해당 층 활성 Slot에 관리 배정을 일괄 생성하는 편의 기능이며, 이후 추가되는 Slot을 자동 포함하지 않는다. | 백엔드 권한 단위를 Slot으로 통일한 MVP 결정 |
| AUTH-010 | MVP | `Confirmed` | 관리자는 거주자가 아니며 거주자 자격을 상속하지 않는다. 계정의 `ADMIN` 권한으로 전체 조회와 별도 운영 행위를 수행한다. 일반 거주자 물품 등록 API를 관리자의 소유물 생성에 사용하지 않는다. | 소유자와 운영 행위자를 분리한 2026-07-26 사용자 합의 |
| AUTH-007 | MVP | `Confirmed` | 보호 API에서 인증 주체를 복원하지 못하면 `401`, 인증됐지만 API를 호출할 역할·업무 자격이 없으면 `403`을 반환한다. 권한 범위 안에 조회할 데이터가 없는 경우는 `200` 빈 목록이다. | Slot 조회 오류 의미 합의 |
| AUTH-008 | MVP | `Confirmed` | 세션 쿠키를 자동 전달하는 브라우저 요청은 CSRF 보호를 유지한다. SPA는 발급받은 CSRF 토큰을 변경 요청 헤더에 전달하며 로그인과 로그아웃도 CSRF 검증 대상이다. | Spring Security 세션 인증의 보안 경계 |

### 5.5 검사

| ID | Scope | Status | Policy | Evidence / Notes |
| --- | --- | --- | --- | --- |
| INSP-001 | MVP | `Confirmed` | 냉장고 담당자는 자신에게 활성 관리 배정된 Slot만 검사하고 처리할 수 있다. 층 전체 검사에서 필요한 관리 범위와 일부 Slot 담당자의 검사 흐름은 Phase 3에서 확정한다. | Feature Inventory의 층 담당 의도를 Slot 단위 관리 권한에 맞게 재설계 |
| INSP-002 | MVP | `Confirmed` | 모든 검사 대상에 PASS, WARNING 또는 DISPOSE 조치가 기록되어야 제출할 수 있다. | Feature Inventory와 실제 프론트 제출 조건 |
| INSP-003 | MVP | `Confirmed` | 제출된 검사 결과는 MVP에서 일반 수정할 수 없다. 정정 흐름은 별도 확장 정책으로 다룬다. | MVP 범위 합의 |
| INSP-004 | MVP | `Confirmed` | 중복 검사 시작과 중복 제출이 동일 결과를 두 번 생성하지 않도록 서버가 보장한다. | MVP 범위와 동시성 핵심 시나리오 |
| INSP-005 | MVP | `Review Required` | 검사 잠금의 정확한 대상, 만료 시점, 활동 시 연장 기준과 강제 종료 권한을 확정해야 한다. | Feature Inventory는 30분 잠금을 제시하나 상세 계약 미확정 |
| INSP-006 | Post-MVP | `Post-MVP` | 여러 냉장고 담당자의 검사 합류와 SSE 실시간 동기화는 Release 1에서 제외한다. | Feature Inventory의 다중 층별장 확장 항목 |
| INSP-007 | Post-MVP | `Post-MVP` | 제출 결과 정정과 벌점 재계산은 Release 1에서 제외한다. | MVP 범위 합의 |

### 5.6 관리자와 알림

| ID | Scope | Status | Policy | Evidence / Notes |
| --- | --- | --- | --- | --- |
| ADM-001 | MVP | `Confirmed` | 관리자 API는 서버에서 관리자 권한을 검증한다. UI의 메뉴 숨김만으로 권한을 보장하지 않는다. | 보안 기본 원칙 |
| ADM-002 | MVP | `Review Required` | 최소 관리자 기능별 조회 범위와 강제 삭제·상태 변경의 의미를 확정해야 한다. | README의 MVP 범위는 있으나 세부 정책 미확정 |
| ADM-003 | MVP | `Confirmed` | 관리자는 전체 냉장고 데이터를 조회할 수 있지만 거주자 권한을 상속하지 않는다. 운영상 정정·강제 삭제는 일반 사용자 API와 구분하고 수행자와 사유를 기록한다. 관리자 대리 등록은 실제 요구가 생길 때 대상 거주자를 명시하는 별도 계약으로 검토한다. | 관리자와 데이터 소유자를 분리한 MVP 경계 |
| NOTI-001 | Post-MVP | `Post-MVP` | 알림 저장·읽음 처리는 Release 1 이후 구현한다. | MVP 범위 합의 |
| NOTI-002 | Post-MVP | `Post-MVP` | Web Push 구독과 서버 발송은 별도 계약과 운영 정책을 확정한 뒤 구현한다. | 현재 프론트에는 표시용 Service Worker만 있고 구독 등록 흐름은 확인되지 않음 |

## 6. 확인된 문서·코드 차이

다음 항목은 구현 전에 관련 정책 또는 계약을 반드시 검토한다.

- OpenAPI에 세션 `securitySchemes`와 Slot 조회 보안 요구를 추가했다. 나머지 보호
  API는 각 수직 슬라이스에서 보안 요구를 명시해야 한다.
- OpenAPI의 다수 응답이 구체적인 미디어 타입 대신 `*/*`로 정의되어 있다.
- 일부 요청·응답 스키마의 필수 필드와 오류 계약이 충분히 명시되지 않았다.
- 현재 프론트는 Bearer access token, refresh token, `deviceId`를 사용하므로
  MVP 연동 시 세션 쿠키와 CSRF 헤더 방식으로 교체해야 한다. 제거되는 토큰
  인증 코드는 `AUTH-011`의 Post-MVP 계약이 확정되기 전 재사용하지 않는다.
- 현재 회원가입 화면은 비활성 안내이며 실제 가입 요청을 보내지 않는다.
- OpenAPI의 Bundle·Inspection·Reallocation·Issue 관련 기존 스키마에는 제거하기로
  한 `slotIndex`, `slotLetter` 또는 `slotLabel` 전제가 남아 있다. 각 수직
  슬라이스를 시작할 때 `slotId`, `fridgeId`, `displayName` 기준으로 계약을
  재검토한다.
- 기존 계약의 `compartmentId`, `fridgeCompartmentId`가 현재 모델의
  `slotId`와 동일한 칸을 가리키는지 아직 확정되지 않았다. 관리자·검사·재배분
  수직 슬라이스에서 동일 개념이면 `slotId`로 통일하고, 다른 개념이면 각각의
  도메인 정의와 관계를 먼저 추가한다.

이 목록은 진행 상태표가 아니다. 항목이 해결되면 관련 정책, OpenAPI,
Tech Decisions와 API 구현 현황을 각각의 역할에 맞게 갱신한다.
프론트 구현 관찰 결과와 연동 기술 부채는 `docs/10_Workspace/Frontend_Integration_Baseline.md`에서 관리한다.
