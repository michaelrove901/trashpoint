# Usa una imagen base con Java 21
FROM eclipse-temurin:21-jdk

# Directorio de trabajo en la imagen
WORKDIR /app

# Copia el proyecto al contenedor
COPY . .

# Construye el .jar con Maven
RUN ./mvnw clean package -DskipTests

# Expón el puerto 8080 (el que usa Spring Boot por defecto)
EXPOSE 8080

# Ejecuta el .jar generado
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]