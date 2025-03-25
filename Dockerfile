FROM openjdk:21

WORKDIR /app

COPY techstore.jar .

RUN mkdir "data"

CMD ["java", "-jar", "techstore.jar"]