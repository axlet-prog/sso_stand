# syntax=docker/dockerfile:1

FROM eclipse-temurin:25-jdk AS build
WORKDIR /build

COPY pom.xml ./pom.xml
COPY --chmod=755 j-resource/mvnw ./j-resource/mvnw
COPY j-resource/.mvn/ ./j-resource/.mvn/
COPY j-resource/pom.xml ./j-resource/pom.xml
COPY j-resource/src/ ./j-resource/src/

WORKDIR /build/j-resource
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B package

RUN cp target/j-resource.jar /build/app.jar \
    && java -Djarmode=tools -jar /build/app.jar \
    extract --layers --destination /build/extracted

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl \
    && addgroup -S app && adduser -S -G app app

COPY --from=build /build/extracted/dependencies/ ./
COPY --from=build /build/extracted/spring-boot-loader/ ./
COPY --from=build /build/extracted/snapshot-dependencies/ ./
COPY --from=build /build/extracted/application/ ./

USER app
EXPOSE 9001
ENTRYPOINT ["java", "-jar", "app.jar"]
