FROM openjdk:19
EXPOSE 8080
EXPOSE 81
EXPOSE 82
EXPOSE 83
ADD target/battleship.jar battleship.jar
ENTRYPOINT ["java", "-jar", "/battleship.jar"]