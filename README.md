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
 - mvn clean install -DskipTests=true jib:dockerBuild -Djib.to.image=authentication_api:v1 
 
#### Development
 - docker run --name authentication_api --env-file .env  --network="host" regisrufino/authentication_api:latest

#### Production
 - docker run --name authentication_api_prod --env-file .prod.env -p 5000:5010 regisrufino/authentication_api:latest

#### Build application docker image (docker hub):
 - mvn clean install -DskipTests=true jib:build -Djib.to.image=regisrufino/authentication_api:latest 

#### Pull
docker pull regisrufino/authentication_api:latest

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
* EMAIL_SERVICE=
* EMAIL_SERVICE_PASSWORD=
* EMAIL_TEST=
* API_UPLOAD_URL=
* ALLOWED_ORIGINS_URL=

