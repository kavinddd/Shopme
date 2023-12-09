FROM openjdk:17-jdk-slim

WORKDIR /app

COPY /ShopmeWebParent/ShopmeBackEnd/target/*.jar .

EXPOSE 8080

CMD ["java", "-jar", "ShopmeBackEnd-1.0-SNAPSHOT.jar"]
