# 프로젝트 소개

**HMG Softeer 4th Project**

![스크린샷 2024-08-24 오후 9 17 01](https://github.com/user-attachments/assets/5cb6f713-3926-4bfc-9298-33eae6549a86)



2025 셀토스 출시 이벤트 (BE repo)

<br/>
<br/>

## 프로젝트 목표



## 팀원 소개

<div align="center">
  
| 김영빈 <br/> [@eckrin](https://github.com/eckrin) | 강승구 <br/> [@luna156](https://github.com/luna156) |
|:--:|:--:|
| <img src="https://avatars.githubusercontent.com/eckrin"  width=200> | <img src="https://avatars.githubusercontent.com/luna156"  width=200> |

</div>
<br/>

## 1. 기술 스택

- Java 17
- Spring Boot 3.3.2
- Spring Data JPA
- Database
    - Mysql 8.0.35
    - Redis 7.0.15
    - H2 2.2.224
- Github Actions
- AWS
  - ec2
  - s3
  - RDS
  - Codedeploy
 
<br/>

## 2. 서버 아키텍쳐

![image (2)](https://github.com/user-attachments/assets/385e33a3-b8b1-4df0-9870-d54037ef0420)

- 범용 api 서버
    - spring
        - 일반적인 api 요청을 처리하기 위한 웹서버
        - 전화번호 인증의 경우 외부 sms 전송 api 사용
    - redis
        - 전화번호 인증번호를 임시로 저장
- 선착순 api 서버
    - spring
        - 선착순 요청 api용 웹서버
    - redis
        - 선착순 이벤트시 인원수 체크와 동시성 처리를 위한 인메모리 저장소
        - 당일 진행될 퀴즈 정보 캐싱
- RDS
    - mysql
        - 당첨자 저장 등 영구적으로 저장될 모든 데이터를 보관


<br/>

## 3. ERD

![image](https://github.com/user-attachments/assets/cd63a7fd-8609-4dc0-b681-dff875334da8)

<br/>

## 4. 그라운드 룰

### 공통
- 모든 컨벤션은 팀원간 합의를 통해서 변경할 수 있다.

### 코드 컨벤션

- 클래스 : **UpperCamelCase,** and·or와 같은 접속사를 사용하지 않고 25자 내외로 작성한다.
- 함수 : **lowerCamelCase**
- 변수, 상수 : **lowerCamelCase**
- DB 테이블: **lower_snake_case**를 사용하며, tb_name과 같이 접두사를 붙이지 않으며, 복수형으로 지정한다.
- ENUM, 상수: **Upper_snake_case**
- 컬렉션(Collection): **복수형**을 사용하거나 **컬렉션을 명시합니다**. (Ex. userList, users, userMap)
- LocalDateTime: 접미사에 **Date**를 붙입니다.

<br/>

### 커밋 컨벤션

**`태그: 제목`의 형태이며, `:`뒤에 space를 추가한다.**

**태그는 다음과 같다.**

- `feat` : 기능 추가
- `fix` : 기존 코드 수정, 버그 수정 등
- `docs` : 문서 추가, 수정
- `style` : 코드 포맷팅, 오탈자 수정, 코드 로직상의 변경이 없는 경우
- `refactor` : 코드 리팩토링
- `test` : 테스트 코드 관련 작업
- `chore` : 빌드 업무 등 기타 작업

<br/>

### 브랜치 전략
![Untitled](https://github.com/user-attachments/assets/4be0cbab-c7e5-4710-b4db-a41a2ce49b4e)


→ **Github Flow** 전략을 사용한다. 
→ PR시 Issue 번호를 명시하고, 코드리뷰 진행후 머지한다.

→ master는 production 브랜치의 역할을 하고, 기능별로 feature브랜치를 뽑아 작업한다.

<br/>

### 브랜치 명명규칙

→ 브랜치는 [branch]/[detail]과 같이 브랜치명을 앞에 명시하고, 뒤에 작업 내용을 적는다. [ex) feat/chat]

<br/>

### 작업 순서

→ 이슈를 작성한 후, Assignee를 지정하고 Github Project 백로그에 저장한다.

→ 스프린트 계획에 따라 계획된 팀원이 정해진 명명규칙에 따라서 브랜치를 만들고, 작업에 들어간다.

→ 작업이 완료되었다면 이슈 번호를 명시하고 Pull Request를 작성한다.

→ 팀원의 코드리뷰 후 문제가 없다면 머지하고, 논의할 사항이 있다면 별도의 시간을 할당하여 합의한다.

<br/>

## 5. 프로젝트 구조

**[Lots-Server]**

```
.
├── build
│   ├── classes
│   │   └── java
│   │       ├── main
│   │       │   └── com
│   │       │       └── softeer
│   │       │           └── podo
│   │       │               ├── admin
│   │       │               │   ├── controller
│   │       │               │   ├── exception
│   │       │               │   ├── model
│   │       │               │   │   ├── dto
│   │       │               │   │   │   ├── request
│   │       │               │   │   │   └── response
│   │       │               │   │   └── mapper
│   │       │               │   ├── service
│   │       │               │   └── validation
│   │       │               ├── common
│   │       │               │   ├── entity
│   │       │               │   ├── exception
│   │       │               │   ├── response
│   │       │               │   └── utils
│   │       │               ├── config
│   │       │               ├── event
│   │       │               │   ├── controller
│   │       │               │   ├── exception
│   │       │               │   ├── model
│   │       │               │   │   ├── dto
│   │       │               │   │   │   ├── request
│   │       │               │   │   │   └── response
│   │       │               │   │   ├── entity
│   │       │               │   │   └── mapper
│   │       │               │   ├── repository
│   │       │               │   ├── scheduler
│   │       │               │   ├── service
│   │       │               │   └── util
│   │       │               ├── log
│   │       │               │   ├── aop
│   │       │               │   ├── filter
│   │       │               │   ├── mapper
│   │       │               │   ├── model
│   │       │               │   │   ├── dto
│   │       │               │   │   └── entity
│   │       │               │   ├── repository
│   │       │               │   └── service
│   │       │               ├── security
│   │       │               │   └── jwt
│   │       │               │       └── exception
│   │       │               ├── test
│   │       │               │   ├── controller
│   │       │               │   ├── exception
│   │       │               │   ├── model
│   │       │               │   │   └── dto
│   │       │               │   │       ├── request
│   │       │               │   │       └── response
│   │       │               │   ├── repository
│   │       │               │   └── service
│   │       │               └── verification
│   │       │                   ├── controller
│   │       │                   ├── exception
│   │       │                   ├── facade
│   │       │                   ├── model
│   │       │                   │   └── dto
│   │       │                   │       ├── request
│   │       │                   │       └── response
│   │       │                   └── service

```

**[Arrival-Server]**

```
.
├── build
│   ├── classes
│   │   └── java
│   │       ├── main
│   │       │   └── com
│   │       │       └── softeer
│   │       │           └── podoarrival
│   │       │               ├── common
│   │       │               │   ├── entity
│   │       │               │   └── response
│   │       │               ├── config
│   │       │               ├── event
│   │       │               │   ├── controller
│   │       │               │   ├── exception
│   │       │               │   ├── model
│   │       │               │   │   ├── dto
│   │       │               │   │   └── entity
│   │       │               │   ├── repository
│   │       │               │   ├── scheduler
│   │       │               │   └── service
│   │       │               ├── mapper
│   │       │               ├── security
│   │       │               │   └── jwt
│   │       │               │       └── exception
│   │       │               └── test
│   │       │                   ├── controller
│   │       │                   └── exception

```



<br/>
