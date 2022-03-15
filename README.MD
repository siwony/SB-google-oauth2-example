# SB OAuth2 Google Example

Spring Boot OAuth2를 이용한 Google 로그인 에제

### 프로젝트 구조
```tree
├── SbOauth2GoogleApplication.java
├── domain
│   ├── auth
│   │   ├── dto
│   │   │   ├── OAuthAttributes.java OAuth 속성을 가지고 있는 DTO
│   │   │   └── SessionMember.java // 로그인 된 사용자의 정보를 세션에 저장할 DTO
│   │   └── service
│   │       └── CustomOAuth2UserService.java // OAuth 로그인 이후 사용자의 정보를 기반으로 가입, 저장 등의 기능을 구현하는 서비스
│   └── member
│       ├── entity
│       │   ├── Member.java
│       │   └── MemberRepository.java
│       └── service
└── global
    └── security
        └── SecurityConfig.java
```

### 의존성 
```groovy
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
//	implementation 'org.springframework.boot:spring-boot-starter-data-redis' // 세션 저장소를 레디스로 사용할 예정
	implementation 'org.springframework.boot:spring-boot-starter-hateoas'
	implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
	implementation 'org.springframework.boot:spring-boot-starter-security'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.springframework.session:spring-session-data-redis'
	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'com.h2database:h2'
	runtimeOnly 'mysql:mysql-connector-java'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testImplementation 'org.springframework.security:spring-security-test'
```