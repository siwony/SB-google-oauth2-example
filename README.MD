# SB OAuth2 Google Example with redis

Spring Boot OAuth2를 이용한 Google 로그인 + redis session storage 에제

### 프로젝트 구조
```tree
/src/main/java/me/siwony/sbaouth2google
├── SbOauth2GoogleApplication.java // application 진입점
├── domain
│   ├── auth
│   │   ├── dto
│   │   │   ├── OAuthAttributes.java // OAuth 속성을 가지고 있는 DTO
│   │   │   └── SessionMember.java // 로그인 된 사용자의 정보를 세션에 저장할 DTO
│   │   └── service
│   │       ├── AuthService.java
│   │       └── CustomOAuth2UserService.java
│   └── member
│       ├── controller
│       │   └── MemberController.java
│       ├── entity
│       │   ├── Member.java
│       │   └── MemberRepository.java
│       └── service
│           └── MemberService.java
└── global
    └── security
        └── SecurityConfig.java
        
/src/main/resources
├── application.yml
└── application-google-oauth.yml
```

#### application-google-oauth.yml
google oauth 인증에 필요한 정보들을 담고있는 에플리케이션 구성 파일입니다.

```yml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: GCP에서 발급받은 client-id
            client-secret: client-secret
            scope:
              - profile
              - email
```
client-id와 client-secret은 
> GCP 콘솔 &rarr; 대시보드 &rarr; API 및 서비스 &rarr; 사용자 인증 정보 
 
에서 확인할 수 있다.
### 주요 의존성
- spring boot starter web
- spring boot data jpa
- spring boot starter security
- spring boot starter oauth2 client
- spring boot starter data redis
- spring session data redis

#### 주의사항
`spring session data redis`의존성 사용시 `spring boot starter data redis`의존성이 반드시 필요합니다.  
없다면 Spring Boot Application 시작 시점에 `org.springframework.beans.factory.BeanCreationException`이 발생하며 종료됩니다.

```log
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.boot.autoconfigure.session.SessionAutoConfiguration$ServletSessionRepositoryValidator': Invocation of init method failed; nested exception is org.springframework.boot.autoconfigure.session.SessionRepositoryUnavailableException: No session repository could be auto-configured, check your configuration (session store type is 'redis')
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1804) ~[spring-beans-5.3.16.jar:5.3.16]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:620) ~[spring-beans-5.3.16.jar:5.3.16]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:542) ~[spring-beans-5.3.16.jar:5.3.16]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335) ~[spring-beans-5.3.16.jar:5.3.16]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-5.3.16.jar:5.3.16]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333) ~[spring-beans-5.3.16.jar:5.3.16]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208) ~[spring-beans-5.3.16.jar:5.3.16]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:953) ~[spring-beans-5.3.16.jar:5.3.16]
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:918) ~[spring-context-5.3.16.jar:5.3.16]
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:583) ~[spring-context-5.3.16.jar:5.3.16]
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:145) ~[spring-boot-2.6.4.jar:2.6.4]
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:740) ~[spring-boot-2.6.4.jar:2.6.4]
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:415) ~[spring-boot-2.6.4.jar:2.6.4]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:303) ~[spring-boot-2.6.4.jar:2.6.4]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1312) ~[spring-boot-2.6.4.jar:2.6.4]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1301) ~[spring-boot-2.6.4.jar:2.6.4]
	at me.siwony.sboauth2google.SbOauth2GoogleApplication.main(SbOauth2GoogleApplication.java:10) ~[main/:na]
Caused by: org.springframework.boot.autoconfigure.session.SessionRepositoryUnavailableException: No session repository could be auto-configured, check your configuration (session store type is 'redis')
	at org.springframework.boot.autoconfigure.session.SessionAutoConfiguration$AbstractSessionRepositoryValidator.afterPropertiesSet(SessionAutoConfiguration.java:315) ~[spring-boot-autoconfigure-2.6.4.jar:2.6.4]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1863) ~[spring-beans-5.3.16.jar:5.3.16]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1800) ~[spring-beans-5.3.16.jar:5.3.16]
	... 16 common frames omitted
```

### Reference
- https://velog.io/@swchoi0329/스프링-시큐리티와-OAuth-2.0으로-로그인-기능-구현
- https://github.com/spring-projects/spring-session/issues/853
- https://docs.spring.io/spring-session/reference/guides/boot-redis.html
- https://docs.spring.io/spring-boot/docs/2.5.6/reference/htmlsingle/#features.spring-session