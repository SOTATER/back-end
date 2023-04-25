# back-end

## DB 설정
### docer-compose env 설정
`.env.default` 파일의 주석 정보를 참고하여 .env 파일 작성
### DB container run
```bash
docker-compose up -d --build postgres
```
### DB initialization
`db-init.sh` 실행 (첫 실행시에만)


## 서버 설정하기
### Local 실행
- src/main/resources/application-local.yaml 수정
  - datasource 설정
    - DB 서버의 endpoint(ip:port)와 DB 컨테이너 실행시 `.env`에서 설정한 user/password 입력
    - db 폴더의 docker-compose를 통해 db를 띄웠다면 localhost:15432일 것
  - riot api key 설정
    - Riot developer portal에서 1일짜리 key 값 생성
    - 생성된 key값 입력
### Docker로 실행
- src/main/resources/application-local.yaml 수정
  - datasource 설정
    - URL: `jdbc:postgresql://postgres:5432/postgres`
    - username/password: DB 컨테이너 실행시 `.env`에서 설정한 username/password 설정
  - riot api key 설정
    - Riot developer portal에서 1일짜리 key 값 생성
    - 생성된 key값 입력
    
## 서버 실행하기
### Linux bash or git-bash in Windows

* 프로젝트 루트에서 `./gradlew bootRun` 입력

### CMD or Powershell in Windows


* 프로젝트 루트에서 `.\gradlew bootRun` 입력

### Docker로 실행
```bash
docker-compose up -d --build app
```


### API 문서 확인하기

* 서버 실행한 상태에서 브라우저를 통해 http://localhost:8080/swagger-ui/ 로 접속
