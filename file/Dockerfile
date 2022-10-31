FROM openjdk:17-jdk-slim as builder
ADD /build/libs/graal-function-0.0.1-SNAPSHOT.jar pepe.jar

RUN java -Djarmode=layertools -jar pepe.jar extract

FROM openjdk:17-jdk-slim
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
