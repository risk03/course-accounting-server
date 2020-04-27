FROM openjdk:8
ADD target/accounting-server.jar accounting-server.jar
EXPOSE 9966
ENTRYPOINT ["java", "-jar", "accounting-server.jar"]