## 1. Architecture Diagram & Components

```mermaid
graph TD
    User["User / Browser"] -->|HTTP Request| Client["Frontend (Next.js)"]
    Client -->|"REST API (JSON)"| Server["Backend (Spring Boot)"]
    
    subgraph Docker Infrastructure
        Server -->|"JPA/JDBC"| DB[("PostgreSQL")]
        Server -->|"Session I/O"| Redis[("Redis")]
    end
````

### Component Roles

|**구분**|**컴포넌트**|**역할 및 기술 스택**|
|---|---|---|
|**Client**|**Frontend**|사용자의 입력을 받고 화면을 렌더링합니다. (Next.js - Legacy)|
|**Server**|**Backend API**|비즈니스 로직을 수행하고 데이터를 가공합니다. (Spring Boot 3.2)|
|**Data**|**PostgreSQL**|영구적인 데이터(물품, 사용자 정보)를 저장합니다.|
|**Cache**|**Redis**|(Phase 2) 로그인 세션 및 분산 락(Distributed Lock) 처리를 담당합니다.|

---

## 2. Data Flow (Request Lifecycle)

사용자의 요청이 처리되는 핵심 흐름입니다.

1. **Request:** 클라이언트(Next.js)가 REST API(`GET /fridge/bundles`)를 호출합니다.
2. **Controller:** 요청 DTO(`BundleRequest`)를 검증(@Valid)하고 Service 계층으로 위임합니다.
3. **Service:** 비즈니스 로직을 수행하며, 필요시 `Repository`를 호출합니다.
    
    - _Transaction 시작_
        
4. **Repository:** JPA/QueryDSL을 통해 DB에서 `Entity` 데이터를 조회합니다.
5. **Mapping:** Service 계층에서 조회된 `Entity`를 응답 전용 `DTO`로 변환합니다. (수동 매핑)
    
    - _Transaction 종료 (Commit)_
        
6. **Response:** Controller가 `DTO`를 JSON 형태로 클라이언트에 반환합니다.
    

---

## 3. Technical Trade-off (기술적 선택의 이유)

초기 개발 단계에서 **오버엔지니어링(Over-Engineering)을 방지**하고, 기술의 **도입 필요성(Pain Point)**을 명확히 하기 위해 아래와 같은 스택을 우선 선택했습니다.

### 3-1. Spring Data JPA (vs QueryDSL)

- **선택 이유:** 개발 생산성 및 복잡도 관리
- **기술적 근거:**
    - **빌드 복잡도 제거:** 초기 CRUD 단계에서 Annotation Processing 설정 등의 빌드 오버헤드를 제거하여 개발 속도를 확보했습니다.
    - **문제 식별:** 추후 복잡한 동적 쿼리 발생 시, JPA의 한계를 명확히 인지하고 QueryDSL을 도입하기 위함입니다.
        

### 3-2. HTTP Session (vs Redis)

- **선택 이유:** 인프라 의존성 최소화 및 I/O 비용 절감
- **기술적 근거:**
    - **Memory Access:** 단일 서버 환경에서는 외부 네트워크(Redis) 통신보다 JVM Heap 메모리 접근이 훨씬 빠릅니다.
    - **Complexity:** 분산 환경(Scale-out)이 아닌 단계에서 세션 클러스터링 도입은 불필요한 비용입니다.
        

### 3-3. Manual Mapping (vs MapStruct)

- **선택 이유:** 데이터 흐름의 명시성(Explicitness) 확보
- **기술적 근거:**
    - **캡슐화 제어:** `Entity` → `DTO` 변환 로직을 직접 작성함으로써, 민감한 정보(Password 등)가 외부로 노출되는 실수를 물리적으로 차단합니다.
    - **디버깅:** 컴파일 타임에 생성되는 매퍼 코드보다, 직접 작성한 코드가 데이터 흐름을 추적(Debugging)하기 유리합니다.
        

### 3-4. ddl-auto: update (vs Flyway)

- **선택 이유:** 스키마 변경의 유연성(Agility) 확보
- **기술적 근거:**
    - **Velocity:** 초기 프로토타이핑 단계의 잦은 스키마 변경에 대응하기 위해, 마이그레이션 스크립트 작성 비용을 줄이고 JPA 자동 동기화를 활용했습니다.
        

### 3-5. Docker Compose (vs AWS)

- **선택 이유:** 환경 격리(Isolation) 및 이식성(Portability)
- **기술적 근거:**
    - **Environment as Code:** 인프라 구성을 코드로 관리하여, 개발 장비 교체나 포맷 후에도 명령어 한 번으로 즉시 개발 환경을 복구할 수 있습니다.
    - **Clean Localhost:** Host OS에 DB 등을 직접 설치하지 않아 시스템 오염을 방지하고, 버전 충돌 없이 깔끔한 독립 환경을 유지합니다.
