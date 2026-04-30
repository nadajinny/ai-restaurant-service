# AI Restaurant Service

AI 기반 식당 서비스 시스템 프로젝트다.  
고객용 웹, 관리자용 웹, Spring Boot 백엔드, FastAPI AI 서버를 분리해서 개발한다.

초기 구조는 MSA 지향이지만, 백엔드는 하나의 Spring Boot 프로젝트 안에서 도메인별 패키지로 나눠 구현되어 있다.

## 1. 프로젝트 소개

이 프로젝트는 식당 메뉴 조회, 주문, 재주문, 리뷰, 즐겨찾기, 알림, 쿠폰, 결제, 관리자 분석 기능을 제공한다.  
또한 AI 서버를 통해 메뉴 추천, 감정 기반 추천, 리뷰 초안 생성, 리뷰 요약, 신메뉴 추천 기능을 제공한다.

구성 요소는 다음 4개다.

- 고객용 웹: `customer-web`
- 관리자용 웹: `admin-web`
- 백엔드 API 서버: `backend`
- AI 서버: `ai-server`

## 2. 기술 스택

### Frontend

- Vue 3
- Vite
- Vue Router

### Backend

- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA
- JWT

### AI Server

- Python 3.11+
- FastAPI
- Uvicorn

### Database / Cache

- SQLite
- Redis

## 3. 시스템 아키텍처 요약

```text
Customer Web (Vue)      Admin Web (Vue)
        |                     |
        +----------+----------+
                   |
            Spring Boot Backend
                   |
        +----------+-----------+
        |                      |
     SQLite                 Redis
        |
     FastAPI AI Server
```

설명:

- 고객용/관리자용 웹은 모두 백엔드 API를 호출한다.
- 백엔드는 주문, 메뉴, 리뷰, 재고, 쿠폰, 결제, 분석 로직을 담당한다.
- AI 관련 요청은 백엔드가 FastAPI AI 서버로 전달한다.
- Redis가 있으면 캐시를 사용하고, 없어도 로컬 개발에서는 fallback으로 동작한다.

## 4. 주요 기능

### 고객 기능

- 메뉴 목록 조회, 카테고리 필터, 정렬
- 메뉴 상세 조회
- 장바구니 관리
- 주문 생성, 주문 상태 조회, 주문 이력 조회, 재주문
- 리뷰 작성/수정/삭제
- 즐겨찾기 추가/해제/조회
- 알림 조회 및 읽음 처리
- AI 메뉴 추천
- 감정 기반 추천
- AI 리뷰 초안 생성

### 관리자 기능

- 관리자 대시보드
- 메뉴 등록/수정/삭제/상태 변경
- 주문 목록 조회 및 상태 변경
- 리뷰 조회 및 숨김 처리
- 재고 수정, 품절 처리, 판매 가능 처리
- 매출 분석, 인기 메뉴 분석, 시간대별 주문량 분석
- 쿠폰 생성/수정/비활성화
- AI 신메뉴 추천 조회

## 5. 프로젝트 구조

```text
.
├── README.md
├── SRS.md
├── backend
├── ai-server
├── customer-web
└── admin-web
```

## 6. 사전 준비

다음이 먼저 설치되어 있어야 한다.

- Java 17
- Node.js 20 이상
- Python 3.11 이상
- Redis (선택)

버전 확인 명령어:

```bash
java -version
node -v
python3 --version
redis-server --version
```

## 7. 환경 변수 설정 방법

### 7-1. 고객용 웹

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/customer-web
cp .env.example .env
```

기본 값:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_AI_SERVER_BASE_URL=http://localhost:8000
```

### 7-2. 관리자용 웹

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/admin-web
cp .env.example .env
```

기본 값:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_AI_SERVER_BASE_URL=http://localhost:8000
```

### 7-3. AI 서버

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/ai-server
cp .env.example .env
```

기본 값:

```env
AI_PROVIDER=mock
OPENAI_API_KEY=your-openai-api-key
BACKEND_BASE_URL=http://localhost:8080
BACKEND_CONNECT_TIMEOUT=3.0
BACKEND_READ_TIMEOUT=5.0
```

### 7-4. 백엔드

백엔드는 기본적으로 `backend/src/main/resources/application.yml`을 사용한다.  
환경 변수로 필요한 값을 덮어쓸 수 있다.

주요 환경 변수:

```bash
export RESTAURANT_DB_PATH=/absolute/path/to/restaurant.db
export JWT_SECRET=change-this-secret
export JWT_ACCESS_TOKEN_EXPIRATION_MS=21600000
export AI_SERVER_BASE_URL=http://localhost:8000
export CACHE_ENABLED=true
export CACHE_REDIS_ENABLED=true
```

Redis 없이 실행하려면:

```bash
export CACHE_REDIS_ENABLED=false
```

## 8. 백엔드 실행 방법

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/backend
./gradlew bootRun
```

기본 주소:

- Backend: [http://localhost:8080](http://localhost:8080)
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

헬스 체크:

```bash
curl http://localhost:8080/api/v1/health
```

Swagger 확인:

```bash
open http://localhost:8080/swagger-ui.html
```

참고:

- SQLite DB 파일은 실행 위치와 무관하게 기본적으로 `backend/restaurant.db`로 생성된다.
- 다른 위치를 쓰려면 `RESTAURANT_DB_PATH` 환경 변수에 절대 경로를 지정한다.
- Redis가 없어도 `CACHE_REDIS_ENABLED=false` 또는 fallback 구조로 개발 가능하다.

### 8-1. Swagger 확인 방법

1. 백엔드를 먼저 실행한다.

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/backend
./gradlew bootRun
```

2. 브라우저에서 아래 주소 중 하나로 접속한다.

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Swagger UI 대체 경로: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

3. 인증이 필요한 API를 테스트하려면 먼저 로그인한다.

- `POST /auth/login` 실행
- 응답의 `accessToken` 복사
- Swagger 우측 상단 `Authorize` 버튼 클릭
- `Bearer <accessToken>` 형식으로 입력

참고:

- `application.yml`에서 `springdoc.swagger-ui.enabled`는 `false`지만, 백엔드의 `SwaggerUiController`가 `/swagger-ui.html`과 `/swagger-ui/index.html`을 직접 제공한다.
- Swagger 페이지가 열리지 않으면 백엔드가 `8080` 포트에서 정상 실행 중인지 먼저 확인한다.

## 9. 프론트엔드 실행 방법

### 9-1. 고객용 웹 실행

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/customer-web
cp .env.example .env
npm install
npm run dev
```

기본 주소:

- Customer Web: [http://localhost:5173](http://localhost:5173)

### 9-2. 관리자용 웹 실행

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/admin-web
cp .env.example .env
npm install
npm run dev
```

기본 주소:

- Admin Web: [http://localhost:5174](http://localhost:5174)

## 10. AI 서버 실행 방법

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/ai-server
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
uvicorn app.main:app --reload --port 8000
```

기본 주소:

- AI Server: [http://localhost:8000](http://localhost:8000)

Mock 모드 설명:

- 현재는 실제 GPT 연동 없이 Mock 응답으로도 동작한다.
- `AI_PROVIDER=mock` 상태면 로컬 개발이 가능하다.

## 11. 전체 실행 순서

초보자 기준 권장 순서:

### 11-1. AI 서버 실행

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/ai-server
source .venv/bin/activate
uvicorn app.main:app --reload --port 8000
```

### 11-2. 백엔드 실행

새 터미널:

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/backend
./gradlew bootRun
```

### 11-3. 고객용 웹 실행

새 터미널:

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/customer-web
npm install
npm run dev
```

### 11-4. 관리자용 웹 실행

새 터미널:

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/admin-web
npm install
npm run dev
```

## 12. 기본 로그인 계정

백엔드 초기 데이터로 다음 계정이 생성된다.

- 고객 계정: `user01 / password`
- 관리자 계정: `admin01 / password`

로그인 API 예시:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginId": "user01",
    "password": "password"
  }'
```

## 13. API 문서 위치

현재 Swagger / OpenAPI UI는 추가되어 있지 않다.  
대신 아래 문서를 기준으로 API를 확인하면 된다.

- 요구명세서: [SRS.md](/Users/leejinsun/Desktop/Develop/campus/oss/restaurant/SRS.md)
- 프로젝트 실행 및 주요 엔드포인트 요약: [README.md](/Users/leejinsun/Desktop/Develop/campus/oss/restaurant/README.md)
- 백엔드 컨트롤러 코드: [backend/src/main/java/com/restaurant/backend](/Users/leejinsun/Desktop/Develop/campus/oss/restaurant/backend/src/main/java/com/restaurant/backend)

대표 엔드포인트 예시:

- `POST /auth/login`
- `GET /menus`
- `POST /orders`
- `POST /reviews`
- `POST /favorites`
- `GET /notifications`
- `POST /ai/recommend`
- `GET /admin/dashboard`

## 14. 테스트 실행 방법

### 14-1. 백엔드 테스트

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/backend
./gradlew test
```

깨끗하게 다시 실행하려면:

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/backend
./gradlew clean test
```

### 14-2. 고객용 웹 빌드 테스트

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/customer-web
npm install
npm run build
```

### 14-3. 관리자용 웹 빌드 테스트

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/admin-web
npm install
npm run build
```

### 14-4. AI 서버 문법 확인

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant
python3 -m compileall ai-server/app
```

## 15. 개발 시 주의사항

- 백엔드는 `controller`, `service`, `repository`, `domain`, `dto` 계층을 유지해야 한다.
- 엔티티를 그대로 API 응답으로 반환하지 말고 DTO를 사용해야 한다.
- 클라이언트가 보낸 가격, 할인 금액, 총액은 신뢰하지 말고 서버에서 다시 계산해야 한다.
- 주문, 리뷰, 즐겨찾기, 알림, 개인화 추천은 인증이 필요한 API다.
- `/admin/**` API는 관리자 권한이 필요하다.
- Redis는 선택 사항이지만, 운영 환경에서는 사용하는 것을 권장한다.
- `.env`, `node_modules`, `build`, `dist`, `restaurant.db`는 Git에 올리지 않는다.
- AI 기능은 실제 GPT 연동 전에도 Mock 또는 Stub으로 동작할 수 있게 유지해야 한다.
- 기능 추가 후에는 최소한 아래 명령은 다시 실행하는 것이 좋다.

```bash
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/backend && ./gradlew test
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/customer-web && npm run build
cd /Users/leejinsun/Desktop/Develop/campus/oss/restaurant/admin-web && npm run build
```

## 16. 참고 문서

- 요구명세서: [SRS.md](/Users/leejinsun/Desktop/Develop/campus/oss/restaurant/SRS.md)
