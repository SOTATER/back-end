## How to set up DB
### docker-compose env setting
`.env.default` 파일의 주석 정보를 참고하여 .env 파일 작성
### DB container run
```bash
docker-compose up -d
// Sudo can be needed when you are not root.
```
#### Only postgresql db run without pgadmin
```bash
docker-compose up -d -- postgres
```