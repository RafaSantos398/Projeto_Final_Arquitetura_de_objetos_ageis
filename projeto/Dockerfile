# syntax=docker/dockerfile:1
FROM eclipse-temurin:26-jdk-noble AS build
WORKDIR /build

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw \
    && ./mvnw -B -ntp dependency:go-offline

COPY src/ src/
# Testes usam somente o H2 em memória do perfil dev durante o build.
RUN ./mvnw -B -ntp verify -Dspring.profiles.active=dev

FROM eclipse-temurin:26-jre-noble
WORKDIR /app
COPY --from=build --chown=10001:10001 /build/target/projeto-0.0.1-SNAPSHOT.jar app.jar
USER 10001:10001
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s --start-period=45s --retries=12 \
    CMD bash -c 'exec 3<>/dev/tcp/127.0.0.1/${SERVER_PORT:-${PORT:-8080}}' || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
