---
type: phase
status: ⚪ Planned
scope: MVP
---

# Phase 2 — 인증과 사용자

> 인증된 사용자를 식별하고 역할·소유권 검증의 기반을 제공한다.

## 선행 결정

- `AUTH-001`: 세션 또는 Access/Refresh Token
- `AUTH-002`: 온라인 회원가입 또는 관리자 계정 발급
- `AUTH-005`: 갱신·로그아웃·다중 기기 정책

선행 정책이 `Confirmed`가 되기 전에는 인증 구현을 시작하지 않는다.

## MVP 후보 API

- `POST /auth/login`
- `POST /auth/logout`
- `GET /profile/me`
- 인증 방식이 Token으로 확정되는 경우 `POST /auth/refresh`

## 제외 범위

- 인증 방식 확정 전 Redis Session 도입
- 다중 기기 고급 관리
- 소셜 로그인

## 완료 조건

- 인증 전달 방식과 보안 계약이 OpenAPI에 정의된다.
- 비활성 사용자 로그인 차단과 역할 검증 테스트가 통과한다.
- 사용자 데이터 접근 범위가 서버에서 검증된다.
