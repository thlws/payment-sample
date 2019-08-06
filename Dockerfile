FROM openjdk:8

COPY ./target/*.jar /app.jar

ENV TZ=Asia/Shanghai
ENV JAVA_OPTIONS="-XX:+PrintFlagsFinal -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1"

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /

CMD java -server -Djava.security.egd=file:/dev/./urandom $JAVA_OPTIONS -jar app.jar