# spring-auth-server-base

Spring Security와 JWT를 이용한 인증 서버

### API
### 1. 회원가입

- `POST /api/auth/signup`

**Request Body**
```json
{
  "username": "wjdansrud",
  "password": "securepassword",
  "email": "user@example.com"
}
```

**Response**
```json
{
  "message": "회원가입 성공",
  "data": null,
  "success": true
}
```

---

### 2. 이메일 인증 코드 발송

- `POST /api/auth/signup/email/send`

**Request Body**
```json
{
  "email": "testEmail@spring.com"
}
```

**Response**
```json
{
  "message": "이메일로 인증 코드 발송 성공",
  "data": null,
  "success": true
}
```

---

### 3. 이메일 인증 코드 검증

- `POST /api/auth/signup/code/verify`

**Request Body**
```json
{
  "email": "testEmail@spring.com",
  "code": "LQ0D29"
}
```

**Response**
```json
{
  "message": "이메일 인증 성공",
  "data": null,
  "success": true
}
```

---

### 4. 로그인

- `POST /api/auth/signin`

**Request Body**
```json
{
  "email": "testEmail@spring.com",
  "password": "testpassword"
}
```

**Response**
```json
{
  "message": "로그인 성공",
  "data": {
    "access_token": "xxx.yyy.zzz",
    "refresh_token": "xxx.yyy.zzz",
    "type": "Bearer"
  },
  "success": true
}
```

---

### 5. 토큰 갱신

- (로그인 성공 후 `refresh_token`을 이용해)

**Response**
```json
{
  "message": "토큰 갱신 성공",
  "data": {
    "access_token": "xxx.yyy.zzz",
    "refresh_token": "xxx.yyy.zzz",
    "type": "Bearer"
  },
  "success": true
}
```

---

### 6. 로그아웃

- `POST /api/auth/logout`  
- **Header:** `Authorization: Bearer <accessToken>`

**Request Body**
```json
{
  "refresh_token": "xxx.yyy.zzz"
}
```

**Response**
```json
{
  "message": "로그아웃 성공",
  "data": null,
  "success": true
}
```

### 파일 구조

```
spring-auth-server-base/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/wjdansrud/springauthserverbase/
│       │       ├── SpringAuthServerBaseApplication.java
│       │
│       │       ├── auth/
│       │       │   ├── controller/
│       │       │   │   └── AuthController.java
│       │       │   ├── dto/
│       │       │   │   ├── req/
│       │       │   │   │   ├── CodeRequest.java
│       │       │   │   │   ├── EmailRequest.java
│       │       │   │   │   ├── RefreshToken.java
│       │       │   │   │   ├── SigninRequest.java
│       │       │   │   │   └── SignupRequest.java
│       │       │   │   └── res/
│       │       │   │       └── TokenPair.java
│       │       │   ├── service/
│       │       │   │   ├── AuthService.java
│       │       │   │   └── MailService.java
│       │       │   ├── util/
│       │       │   │   └── CodeGenerator.java
│       │       │   └── config/
│       │       │       ├── SecurityConfig.java
│       │       │       ├── CustomUserDetailsService.java
│       │       │       ├── UserPrincipal.java
│       │       │       └── jwt/
│       │       │           ├── JwtAuthenticationFilter.java
│       │       │           ├── JwtGenerator.java
│       │       │           ├── JwtService.java
│       │       │           ├── JwtUtil.java
│       │       │           └── TokenStatus.java
│       │
│       │       ├── user/
│       │       │   ├── UserRepository.java
│       │       │   └── entity/
│       │       │       ├── Role.java
│       │       │       └── User.java
│       │
│       │       ├── redis/
│       │       │   ├── RedisConfig.java
│       │       │   └── RedisService.java
│       │
│       │       ├── common/
│       │       │   ├── code/
│       │       │   │   ├── AuthErrorCode.java
│       │       │   │   ├── AuthSuccessCode.java
│       │       │   │   ├── ErrorCode.java
│       │       │   │   ├── SuccessCode.java
│       │       │   │   └── UserErrorCode.java
│       │       │   ├── response/
│       │       │   │   ├── ErrorResponse.java
│       │       │   │   └── SuccessResponse.java
│       │       │   ├── exception/
│       │       │   │   ├── AuthException.java
│       │       │   │   └── UserException.java
│       │       │   └── GlobalExceptionHandler.java
│
│       └── resources/
│           └── application.yml

```

### 환경변수

```
spring:
  jackson:
    property-naming-strategy: SNAKE_CASE

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/<DB_NAME>?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
    username: <DB_USERNAME>
    password: <DB_PASSWORD>

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true

  data:
    redis:
      host: <REDIS_HOST>
      port: <REDIS_PORT>
      key:
        REDIS_REFRESH_KEY_BASE: <KEY>
        REDIS_EMAIL_CODE_BASE: <KEY>
        REDIS_EMAIL_VERIFICATION_BASE: <KEY>

  jwt:
    access-token:
      secret: <JWT_ACCESS_SECRET>
      expiration: 3600000  # 1시간 (ms)
    refresh-token:
      secret: <JWT_REFRESH_SECRET>
      expiration: 604800000  # 7일 (ms)

  mail:
    host: smtp.gmail.com
    port: 587
    username: <EMAIL_USERNAME>
    password: <EMAIL_APP_PASSWORD>
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

application:
  name: <PROJECT_NAME>

```

