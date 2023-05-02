FROM openjdk:19
EXPOSE 8080
EXPOSE 81
EXPOSE 82
EXPOSE 83
ADD target/VGame.jar VGame.jar
ENTRYPOINT ["java", "-jar", "/VGame.jar"]