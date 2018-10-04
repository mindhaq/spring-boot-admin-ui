# Spring Boot Admin version 2.x dockerized

Aim of this project is to be able to run [spring-boot-admin](https://github.com/codecentric/spring-boot-admin) application as a Docker container.

Docker image is publicly available as [mindhaq/spring-boot-admin-ui](https://hub.docker.com/r/mindhaq/spring-boot-admin-ui/).

You can run spring-boot-admin in Docker with this command:

`
docker run -d -p 8090:8080 --rm --name spring-boot-admin mindhaq/spring-boot-admin-ui:2.0.3
`

Now just go to <http://localhost:8090> (or <http://your-docker:8090>) with your browser.


# Build your own

`
./gradlew buildDocker
`
