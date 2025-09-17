# syntax=docker/dockerfile:1
################################################################################
FROM eclipse-temurin:17-jdk-jammy AS base

RUN apt-get update && apt-get install -y dos2unix

WORKDIR /build

COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/

RUN dos2unix mvnw && \
    apt-get --purge remove -y dos2unix && \
    rm -rf /var/lib/apt/lists/*

################################################################################
FROM base AS deps

WORKDIR /build

COPY ./src src/

RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw -B dependency:go-offline -DskipTests

################################################################################
FROM deps AS test

WORKDIR /build
COPY ./src src/

RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw test

################################################################################
FROM deps AS package

WORKDIR /build
COPY ./src src/

RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests && \
    mv target/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar target/app.jar

################################################################################
FROM package AS extract

WORKDIR /build

RUN java -Djarmode=layertools -jar target/app.jar extract --destination target/extracted

################################################################################
FROM extract AS development

WORKDIR /build

RUN cp -r /build/target/extracted/dependencies/.          ./
RUN cp -r /build/target/extracted/spring-boot-loader/.    ./
RUN cp -r /build/target/extracted/snapshot-dependencies/. ./
RUN cp -r /build/target/extracted/application/.           ./

ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

CMD [ "java", "org.springframework.boot.loader.launch.JarLauncher" ]

################################################################################
FROM eclipse-temurin:17-jre-jammy AS final

ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser

COPY --from=extract /build/target/extracted/dependencies/ ./
COPY --from=extract /build/target/extracted/spring-boot-loader/ ./
COPY --from=extract /build/target/extracted/snapshot-dependencies/ ./
COPY --from=extract /build/target/extracted/application/ ./

EXPOSE 80

ENTRYPOINT [ "java", "org.springframework.boot.loader.launch.JarLauncher" ]
