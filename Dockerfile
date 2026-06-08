FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew build -x test

ENV SPRING_PROFILES_ACTIVE=prod
CMD ["java", "-jar", "build/libs/BackendSpringBootBots-0.0.1-SNAPSHOT.jar"]
