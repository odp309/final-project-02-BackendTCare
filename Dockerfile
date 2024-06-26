#FROM openjdk:17-alpine
#COPY target/*.jar testapp.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "testapp.jar"]

# Menggunakan image base Java 17
FROM openjdk:17-jdk-alpine

# Menentukan direktori kerja di dalam container
WORKDIR /app

# Menyalin file JAR aplikasi ke dalam container
COPY target/*.jar app.jar

# Menentukan port yang akan di-expose
EXPOSE 8080

# Menentukan perintah untuk menjalankan aplikasi
ENTRYPOINT ["java", "-jar", "app.jar"]

