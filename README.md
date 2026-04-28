# ai-restaurant-service

AI 기반 식당 서비스 시스템 프로젝트다. 현재는 요구명세서와 Spring Boot 백엔드 기본 골격이 포함되어 있다.

## 문서

- [Software Requirements Specification](./SRS.md)

## 현재 구조 요약

```text
.
├── SRS.md
├── README.md
├── ai-server
│   ├── .env.example
│   ├── requirements.txt
│   └── app
│       ├── api
│       ├── clients
│       ├── core
│       ├── schemas
│       └── services
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

## AI 서버 실행 방법

Python 3.11 이상을 권장한다.

```bash
cd ai-server
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
uvicorn app.main:app --reload --port 8000
```

기본 포트는 `8000`이다.

예시 엔드포인트

- `GET /api/v1/health`
- `GET /api/v1/users/sample`
- `GET /menus`
- `GET /menus?category=KOREAN`
- `GET /menus?minPrice=8000&maxPrice=12000&status=AVAILABLE&sort=PRICE_ASC`
- `GET /menus/{menuId}`
- `POST /favorites?userId=1`
- `GET /favorites?userId=1`
- `DELETE /favorites/{menuId}?userId=1`
- `GET /orders?userId=1`
- `GET /orders/{orderId}?userId=1`
- `POST /orders`
- `POST /orders/{orderId}/reorder?userId=1`
- `POST /reviews?userId=1`
- `GET /menus/{menuId}/reviews`
- `PUT /reviews/{reviewId}?userId=1`
- `DELETE /reviews/{reviewId}?userId=1`
- `GET /admin/reviews`
- `PATCH /admin/reviews/{reviewId}/hide`
- `GET /admin/inventories`
- `PUT /admin/inventories/{menuId}`
- `PATCH /admin/inventories/{menuId}/sold-out`
- `PATCH /admin/inventories/{menuId}/available`
- `POST /payments`
- `GET /payments/{paymentId}`
- `POST /payments/{paymentId}/cancel`
- `POST /admin/coupons`
- `PUT /admin/coupons/{couponId}`
- `PATCH /admin/coupons/{couponId}/disable`
- `GET /coupons/available`
- `POST /coupons/apply?userId=1`
- `GET /notifications?userId=1`
- `PATCH /notifications/{notificationId}/read?userId=1`
- `GET /admin/dashboard`
- `GET /admin/analytics/sales`
- `GET /admin/analytics/popular-menus`
- `GET /admin/analytics/menu-performance`
- `GET /admin/analytics/hourly-orders`
- `PATCH /admin/orders/{orderId}/status`
- `POST /admin/menus`
- `PUT /admin/menus/{menuId}`
- `DELETE /admin/menus/{menuId}`
- `PATCH /admin/menus/{menuId}/status`
- `POST /api/v1/ai/recommendations/mock`
- `POST /ai/recommend`
- `GET /ai/personalized-recommendations`
- `POST /ai/emotion-recommend`
- `POST /ai/review-generate`
- `GET /ai/menus/{menuId}/review-summary`
- `GET /admin/ai/new-menu-recommendations`

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

관리자 메뉴 관리 예시

```bash
curl -X POST http://localhost:8080/admin/menus \
  -H "Content-Type: application/json" \
  -d '{
    "name": "새우볶음밥",
    "category": "CHINESE",
    "price": 9500,
    "description": "불향이 나는 새우볶음밥",
    "imageUrl": "https://example.com/fried-rice.jpg",
    "cookingTime": 12,
    "status": "AVAILABLE"
  }'
```

즐겨찾기 예시

```bash
curl -X POST "http://localhost:8080/favorites?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "menuId": 1
  }'

curl "http://localhost:8080/favorites?userId=1"
curl -X DELETE "http://localhost:8080/favorites/1?userId=1"
```

주문 생성 예시

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      { "menuId": 1, "quantity": 2 },
      { "menuId": 3, "quantity": 1 }
    ],
    "couponCode": "WELCOME10"
  }'
```

재주문 예시

```bash
curl -X POST "http://localhost:8080/orders/1/reorder?userId=1"

curl -X POST "http://localhost:8080/orders/1/reorder?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "menuIds": [1, 3]
  }'
```

주문 조회 예시

```bash
curl "http://localhost:8080/orders?userId=1"
curl "http://localhost:8080/orders/1?userId=1"
```

주문 상태 변경 예시

```bash
curl -X PATCH http://localhost:8080/admin/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "COOKING"
  }'
```

리뷰 API 예시

```bash
curl -X POST "http://localhost:8080/reviews?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "menuId": 1,
    "content": "맛있고 양도 충분했습니다.",
    "rating": 5,
    "aiGenerated": false
  }'

curl "http://localhost:8080/menus/1/reviews"

curl -X PUT "http://localhost:8080/reviews/1?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "양도 충분하고 다시 주문하고 싶습니다.",
    "rating": 5,
    "aiGenerated": true
  }'

curl -X DELETE "http://localhost:8080/reviews/1?userId=1"
curl "http://localhost:8080/admin/reviews"
curl -X PATCH "http://localhost:8080/admin/reviews/1/hide"
```

재고 관리 예시

```bash
curl "http://localhost:8080/admin/inventories"

curl -X PUT "http://localhost:8080/admin/inventories/1" \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 15
  }'

curl -X PATCH "http://localhost:8080/admin/inventories/1/sold-out"
curl -X PATCH "http://localhost:8080/admin/inventories/1/available"
```

결제 예시

```bash
curl -X POST "http://localhost:8080/payments" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "mockResult": "APPROVED"
  }'

curl -X POST "http://localhost:8080/payments" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 2,
    "mockResult": "FAILED"
  }'

curl "http://localhost:8080/payments/1"
curl -X POST "http://localhost:8080/payments/1/cancel"
```

쿠폰 예시

```bash
curl -X POST "http://localhost:8080/admin/coupons" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "WELCOME10",
    "name": "웰컴 10퍼센트",
    "discountAmount": null,
    "discountRate": 10,
    "maxDiscountAmount": 3000,
    "minOrderAmount": 10000,
    "availableFrom": "2026-04-01T00:00:00",
    "availableTo": "2026-05-31T23:59:59",
    "availableCount": 100,
    "active": true
  }'

curl -X PUT "http://localhost:8080/admin/coupons/1" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "WELCOME10",
    "name": "웰컴 2000원",
    "discountAmount": 2000,
    "discountRate": null,
    "maxDiscountAmount": null,
    "minOrderAmount": 12000,
    "availableFrom": "2026-04-01T00:00:00",
    "availableTo": "2026-05-31T23:59:59",
    "availableCount": 50,
    "active": true
  }'

curl -X PATCH "http://localhost:8080/admin/coupons/1/disable"
curl "http://localhost:8080/coupons/available"

curl -X POST "http://localhost:8080/coupons/apply?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "couponCode": "WELCOME10"
  }'
```

알림 예시

```bash
curl "http://localhost:8080/notifications?userId=1"
curl -X PATCH "http://localhost:8080/notifications/1/read?userId=1"
```

대시보드 및 분석 예시

```bash
curl "http://localhost:8080/admin/dashboard"
curl "http://localhost:8080/admin/analytics/sales"
curl "http://localhost:8080/admin/analytics/popular-menus"
curl "http://localhost:8080/admin/analytics/menu-performance"
curl "http://localhost:8080/admin/analytics/hourly-orders"
```

AI 서버 예시

```bash
curl -X POST "http://localhost:8000/ai/recommend" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "오늘 매운 음식이 먹고 싶어"
  }'

curl "http://localhost:8000/ai/personalized-recommendations?userId=1"

curl -X POST "http://localhost:8000/ai/emotion-recommend" \
  -H "Content-Type: application/json" \
  -d '{
    "emotion": "stressed",
    "context": "오늘 일이 많았어"
  }'

curl -X POST "http://localhost:8000/ai/review-generate" \
  -H "Content-Type: application/json" \
  -d '{
    "menuId": 1,
    "keywords": ["맛있다", "양이 많다", "친절하다"]
  }'

curl "http://localhost:8000/ai/menus/1/review-summary"
curl "http://localhost:8000/admin/ai/new-menu-recommendations"
```

## 테스트 방법

```bash
cd backend
./gradlew test
```

```bash
python3 -m compileall ai-server/app
```

현재는 메뉴 조회/관리, 주문 생성/조회/재주문/상태 변경, 재고 관리, Mock 결제, 쿠폰/할인, 알림, 대시보드/분석, 즐겨찾기, 리뷰 기능을 포함한 Spring Boot 백엔드와, Mock 기반 추천/리뷰 생성/요약을 제공하는 FastAPI AI 서버 기본 구조가 구현되어 있다. 인증과 실제 GPT 연동은 이후 단계에서 확장할 수 있다.
