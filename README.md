# Spring Boot Admin dockerized

Aim of this project is to be able to run [spring-boot-admin](https://github.com/codecentric/spring-boot-admin) application as a Docker container.

Docker image is publicly available as [luctor/spring-boot-admin-ui](https://hub.docker.com/r/luctor/spring-boot-admin-ui/).

You can run spring-boot-admin in Docker with this command:

`
docker run -d -p 8090:8080 --name spring-boot-admin luctor/spring-boot-admin-ui:1.4.6
`

Now just go to <http://localhost:8090> (or <http://your-docker:8090>) with your browser.

Cloned from blog [post](http://aetas.pl/?p=347).