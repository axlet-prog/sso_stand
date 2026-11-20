# syntax=docker/dockerfile:1

FROM eclipse-temurin:25-jdk AS build
WORKDIR /build

COPY ../pom.xml ./pom.xml

COPY --chmod=755 j-sso/mvnw ./j-sso/mvnw
COPY j-sso/.mvn/ ./j-sso/.mvn/
COPY j-sso/pom.xml ./j-sso/pom.xml

COPY j-sso/src/ ./j-sso/src/
COPY j-sso/database/ ./j-sso/database/

WORKDIR /build/j-sso

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -DskipTests package

RUN cp target/j-sso.jar /build/app.jar \
    && java -Djarmode=tools -jar /build/app.jar \
    extract --layers --destination /build/extracted


FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S -G app app

COPY --from=build /build/extracted/dependencies/ ./
COPY --from=build /build/extracted/spring-boot-loader/ ./
COPY --from=build /build/extracted/snapshot-dependencies/ ./
COPY --from=build /build/extracted/application/ ./

USER app
EXPOSE 7777
ENTRYPOINT ["java", "-jar", "app.jar"]