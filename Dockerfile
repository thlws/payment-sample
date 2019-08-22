FROM openjdk:8

COPY ./target/*.jar /app.jar

ENV TZ=Asia/Shanghai

#此项可以监控jvm
ENV JAVA_OPTIONS="-XX:+PrintGCDetails  -XX:+UnlockExperimentalVMOptions -XX:+PrintFlagsFinal  -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -XX:+PrintHeapAtGC -Xloggc:/root/logs/heap_gc.txt -Dfile.encoding=UTF-8  -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=6006 -Dcom.sun.management.jmxremote  -Dcom.sun.management.jmxremote.rmi.port=1199 -Dcom.sun.management.jmxremote.port=1199 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=thlws.org"
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 1199
EXPOSE 7171
EXPOSE 6006

WORKDIR /

CMD java -server -Djava.security.egd=file:/dev/./urandom $JAVA_OPTIONS -jar app.jar