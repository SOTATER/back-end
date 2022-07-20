# back-end

## DB 설정
[DB 설정 바로가기](db/README.md)

## 서버 설정하기
- src/main/resources/application-local.yaml 수정
  - datasource 설정
    - DB 서버의 endpoint(ip:port)와 DB 설정하기에서 설정한 user/password 입력
    - db 폴더의 docker-compose를 통해 db를 띄웠다면 localhost:15432일 것
  - riot api key 설정
    - Riot developer portal에서 1일짜리 key 값 생성
    - 생성된 key값 입력
- sql 입력
  - table은 jpa의 기능으로 없으면 자동으로 생성되므로 미리 만들어둘 필요 없음.
  - 따로 정의한 Type은 미리 생성하지 않으면 서버 실행이 불가능하므로, types.sql의 sql문을 이용해 생성해둘 것
  - DB에 sql 질의를 위해서는, pgadmin을 이용하거나 beekeeper와 같은 DB의 client studio를 활용할 것
    - beekeeper가 간편하게 사용할 수 있어 추천.
## 서버 실행하기
### Linux bash or git-bash in Windows

* 프로젝트 루트에서 `./gradlew bootRun` 입력

### CMD or Powershell in Windows

* 프로젝트 루트에서 `.\gradlew bootRun` 입력

### Docker로 실행

* `.env.default` 파일의 주석 정보를 참고하여 .env 파일 작성
* 빌드 : `docker-compose build`
* 실행 : `docker-compose up -d`


### API 문서 확인하기

* 서버 실행한 상태에서 브라우저를 통해 http://localhost:8080/swagger-ui/ 로 접속
