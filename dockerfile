FROM openjdk:27-ea-trixie
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} library.jar
ENTRYPOINT ["java","-jar","/library.jar"]
EXPOSE 8094