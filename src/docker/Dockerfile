FROM openjdk:8-jre-alpine
WORKDIR /app
COPY target/generate-documents.jar ./
EXPOSE 8080
ENTRYPOINT java $SYS_PROPS -jar generate-documents.jar

# Note : run it with `docker build -t <your_image_name> -f src/docker/Dockerfile .`
