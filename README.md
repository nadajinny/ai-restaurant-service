# ai-restaurant-service

AI 기반 식당 서비스 시스템 프로젝트다. 현재는 요구명세서와 Spring Boot 백엔드 기본 골격이 포함되어 있다.

## 문서

- [Software Requirements Specification](./SRS.md)

## 현재 구조 요약

```text
.
├── SRS.md
├── README.md
└── backend
    ├── build.gradle
    ├── gradlew
    ├── gradle/wrapper
    └── src
        ├── main
        │   ├── java/com/restaurant/backend
        │   │   ├── common
        │   │   ├── config
        │   │   ├── user
        │   │   ├── menu
        │   │   ├── order
        │   │   ├── review
        │   │   ├── favorite
        │   │   ├── inventory
        │   │   ├── payment
        │   │   ├── coupon
        │   │   ├── notification
        │   │   ├── analytics
        │   │   └── ai
        │   └── resources
        └── test
            └── java/com/restaurant/backend
```

각 도메인 패키지는 기본적으로 다음 계층을 가진다.

- `controller`
- `service`
- `repository`
- `domain`
- `dto`

`common` 패키지에는 다음 공통 구조가 포함되어 있다.

- 일관된 API 응답용 `ApiResponse`
- `ErrorCode`
- `BusinessException`
- `GlobalExceptionHandler`
- `HealthController`

## 공통 응답 형식

성공 응답 예시

```json
{
  "success": true,
  "message": "요청이 성공했습니다.",
  "data": {}
}
```

실패 응답 예시

```json
{
  "success": false,
  "message": "요청한 리소스를 찾을 수 없습니다.",
  "errorCode": "RESOURCE_NOT_FOUND"
}
```

## 백엔드 실행 방법

Java 17이 필요하다.

```bash
cd backend
./gradlew bootRun
```

기본 포트는 `8080`이다.

기본 데이터베이스는 SQLite이며, 실행 시 `backend/restaurant.db` 파일이 생성될 수 있다.

예시 엔드포인트

- `GET /api/v1/health`
- `GET /api/v1/users/sample`
- `GET /menus`
- `GET /menus?category=KOREAN`
- `GET /menus?minPrice=8000&maxPrice=12000&status=AVAILABLE&sort=PRICE_ASC`
- `GET /menus/{menuId}`
- `GET /api/v1/orders/sample`
- `POST /api/v1/ai/recommendations/mock`

헬스 체크 예시

```bash
curl http://localhost:8080/api/v1/health
```

AI 추천 Mock 요청 예시

```bash
curl -X POST http://localhost:8080/api/v1/ai/recommendations/mock \
  -H "Content-Type: application/json" \
  -d '{"message":"매운 음식 추천해줘"}'
```

메뉴 조회 예시

```bash
curl http://localhost:8080/menus
curl "http://localhost:8080/menus?category=KOREAN"
curl "http://localhost:8080/menus?minPrice=8000&maxPrice=12000&status=AVAILABLE&sort=PRICE_ASC"
curl http://localhost:8080/menus/1
```

## 테스트 방법

```bash
cd backend
./gradlew test
```

현재 단계에서는 실제 비즈니스 로직 대신 컴파일 가능한 기본 패키지 구조와 최소 Mock 응답만 구성되어 있다.
