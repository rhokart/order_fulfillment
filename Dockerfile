FROM openjdk:17-alpine
EXPOSE 8087
COPY target/order_fulfillment-0.0.1-SNAPSHOT.jar order_fulfillment.jar
ENTRYPOINT ["java","-jar","order_fulfillment.jar"]
