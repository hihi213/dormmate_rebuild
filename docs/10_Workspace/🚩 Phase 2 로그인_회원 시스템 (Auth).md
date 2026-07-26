---
type: phase
status: ⚪ Planned
scope: MVP
---

# Phase 2 — 인증과 사용자

> 인증된 사용자를 식별하고 역할·소유권 검증의 기반을 제공한다.

## Phase 1 선행 경계

Phase 1 Service는 Tech Decisions 14장의 내부 인증 주체를 통해 `userId`와
authorities를 입력받는다. 거주 배정과 냉장고 담당자의 관리 Slot은 변경 가능한 업무
데이터이므로 Service가 DB에서 조회한다.

`AUTH-001`의 서버 세션 인증, CSRF와 `401`·`403` 의미는 확정됐다.
`GET /fridge/slots` Controller를 완료하려면 Spring Security 구성과 실제 인증
주체 연결을 구현하고 통합 검증해야 한다.

## 선행 결정

- `AUTH-002`: 온라인 회원가입 또는 관리자 계정 발급
- `AUTH-005`: idle timeout, 동시 로그인과 다중 기기 세부 정책
- 로그인·로그아웃과 `/profile/me`의 구체 요청·응답 계약

미확정 세부 정책과 무관한 세션 인증 기반과 Slot 인증 통합은 먼저 구현할 수 있다.

## MVP 후보 API

- `POST /auth/login`
- `POST /auth/logout`
- `GET /csrf`
- `GET /profile/me`

## 제외 범위

- Access/Refresh Token과 `deviceId`
- Redis Session
- 다중 기기 고급 관리
- 소셜 로그인

## 완료 조건

- 인증 전달 방식과 보안 계약이 OpenAPI에 정의된다.
- 비활성 사용자 로그인 차단과 역할 검증 테스트가 통과한다.
- 사용자 데이터 접근 범위가 서버에서 검증된다.
