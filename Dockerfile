FROM amazoncorretto:23

WORKDIR /app

COPY . .

RUN yum install -y findutils

RUN chmod +x ./gradlew

RUN ./gradlew build -x bootJar --no-daemon

CMD [ "java", "-jar", "/app/build/libs/rokasthelper-0.0.1-SNAPSHOT.jar" ]