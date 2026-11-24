# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk AS build
WORKDIR /build

COPY pom.xml ./pom.xml
COPY --chmod=755 j-swagger-ui/mvnw ./j-swagger-ui/mvnw
COPY j-swagger-ui/.mvn/ ./j-swagger-ui/.mvn/
COPY j-swagger-ui/pom.xml ./j-swagger-ui/pom.xml
COPY j-swagger-ui/src/ ./j-swagger-ui/src/

WORKDIR /build/j-swagger-ui
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -DskipTests package

RUN cp target/j-swagger-ui.jar /build/app.jar \
    && java -Djarmode=tools -jar /build/app.jar \
    extract --layers --destination /build/extracted

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl \
    && addgroup -S app && adduser -S -G app app

COPY --from=build /build/extracted/dependencies/ ./
COPY --from=build /build/extracted/spring-boot-loader/ ./
COPY --from=build /build/extracted/snapshot-dependencies/ ./
COPY --from=build /build/extracted/application/ ./

USER app
EXPOSE 7778
ENTRYPOINT ["java", "-jar", "app.jar"]
