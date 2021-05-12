# ITEM - API
Spring boot rest api

## PREREQUISITES
- Java
- Docker | https://docs.docker.com/engine/install/ubuntu/ 

## DOCKER
- RUNNING POSTGRES
- create:
docker run --name authentication_db -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=authentication -d postgres:alpine
- stop:
docker stop authentication_db
- start:
docker start authentication_db

#### Build application local docker image:
 - docker run --name authentication_api --env-file .env -p 5000:5000 authentication_api:v1

## DATABASE DESCRIPTION

|       Users           |
|--------------------   |
|userId: UUID (PK)      |
|userNickname: String   |
|userEmail: String      |
|userPassword: String   |
|createdAt: Timestamp   |

## Needed Environment variables (.env file) 

* DATABASE_URL=
* JWT_SECRET=
* EMAIL_SERVICE_PASSWORD=
* EMAIL_TEST=
* API_UPLOAD_URL=

