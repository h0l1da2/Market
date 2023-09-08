# 역곡 마트
모두가 사랑하는 우리 동네 마트

## 일정
- `2023-09-01` ~ `2023-09-08`


## 기술
- JAVA 17
- Spring Boot 3.1.3
- Spring Data JPA
- H2 Database


## 엔티티 설계

![image](https://github.com/h0l1da2/Market/assets/116418443/946358f4-ef25-4065-a461-3f971c35363c)


## 신경 쓴 점
- **원활한 협업을 위해**
  - 직관적인 변수명, 메서드명을 짓도록 노력했어요.
  - 코드 가독성을 중요하게 생각했어요.


- **해당 상품에 관한 쿠폰은 가격/비율 쿠폰 중 하나만 넣었어요.**
  - 어차피 주문에는 한 가지 쿠폰만 적용되도록 설계했거든요.
  - 유저에게는 주문/상품에 관한 선택지만 주는 것이 낫다고 생각했어요.


- **최대한 많은 테스트를 하려고 노력했어요...**


---

## 실행 방법

1. **프로젝트 클론**
```bash
    git clone https://github.com/h0l1da2/Market.git
```

2. **프로젝트 실행**
```bash
    ./gradlew bootRun
```

3. **테스트**

- 인텔리제이로 프로젝트 내에 있는 `src/http/` 디렉토리 안에 들어있는 `~ApiTest.http` 파일을 여시고
![httpTestGuide](https://github.com/h0l1da2/Market/assets/116418443/f15a1b7b-3c41-4b0f-a178-b1d05fef7514)
- 왼쪽 초록 버튼은 `테스트 하나`, 위 초록 버튼을 누르면 `전체 테스트`가 가능합니다 ! 

![image](https://github.com/h0l1da2/Market/assets/116418443/456bfd45-8ce7-4f5b-90a6-96575c9fab5c)
- FinalApiTest.http 에 테스트를 원활하게 할 수 있는 순서로 Api 를 배치해두었습니다!
- 아이템 ``추가``부터 ~ ``조회`` ~ ``삭제``까지 한번에 볼 수 있답니다.

그럼 재미있게 즐겨주세요 ! 😄
