FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /workspace/app

COPY . /workspace/app
RUN --mount=type=cache,target=/root/.gradle ./gradlew clean build -x test
RUN java -Djarmode=layertools -jar /workspace/app/build/libs/*-SNAPSHOT.jar extract

FROM eclipse-temurin:17-jre-alpine
COPY --from=builder /workspace/app/dependencies/ ./
COPY --from=builder /workspace/app/snapshot-dependencies/ ./
COPY --from=builder /workspace/app/spring-boot-loader/ ./
COPY --from=builder /workspace/app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]