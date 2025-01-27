#
# Build stage
#
FROM maven:3.8.5-openjdk-17-slim AS build
ARG MONGODB_URI
ENV MONGODB_URI ${MONGODB_URI}
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:17-jdk-slim
ARG PORT
ENV PORT ${PORT}
COPY --from=build /home/app/target/java-spring-security-test-*.jar /usr/local/lib/demo.jar
EXPOSE ${PORT}
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar"]