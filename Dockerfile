# we will use openjdk 8 with alpine as it is a very small linux distro
FROM openjdk:8-jre-alpine3.9

# copy the packaged jar file into our docker image
COPY target/pentair-client-0.0.1-SNAPSHOT-jar-with-dependencies.jar /pentair-prom-client.jar

ENV PENT_IP="192.168.1.249" \
    PENT_PORT="6681" \
    PENT_PROM_IP_PORT="192.168.1.2:9090"

EXPOSE 8080

# set the startup command to execute the jar
CMD ["java", "-jar", "/pentair-prom-client.jar"]
