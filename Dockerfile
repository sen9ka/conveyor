FROM openjdk:17
EXPOSE 8080
ADD /target/conveyor-3.0.0.jar conveyor-3.0.0.jar
ENTRYPOINT ["java", "-jar", "conveyor-3.0.0.jar"]