---
type: phase
status: ⚪ Planned
scope: MVP
---

# Phase 3 — 냉장고 검사

> 층별장이 검사 대상을 처리하고 중복 없이 제출할 수 있게 한다.

## MVP 범위

- 검사 시작
- 검사 세션과 대상 조회
- PASS·WARNING·DISPOSE 조치 기록
- 검사 제출
- 중복 시작·중복 제출 방지
- 제출 이후 일반 수정 금지

## 선행 결정

- `INSP-005`: 잠금 대상, 만료와 연장, 강제 종료 정책
- Slot 조회에 `slotStatus`, `locked`, `lockedUntil`을 추가할 필요와 계산 원천

## Post-MVP

- 다중 층별장 합류와 SSE
- 제출 결과 정정
- 벌점 재계산
- Redis Lock 비교 실험

## 완료 조건

- 활성 Slot 관리 배정과 모든 대상 처리 조건을 서버가 검증한다.
- 중복 시작과 제출에 대한 동시 요청 테스트가 통과한다.
- DB 트랜잭션·제약·락 방식의 선택 근거가 기록된다.
