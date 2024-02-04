package com.shutterbug.notificationservice;

import com.shutterbug.notificationservice.event.OrderPlaceEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
public class NotificationApplication {

    public static void main ( String[] args ) {
        SpringApplication.run(NotificationApplication.class, args);
    }
    
    @KafkaListener(topics = "notificationTopic", groupId = "notificationId")
    public void handleMessage( OrderPlaceEvent orderPlaceEvent) {
        // send out email or slack
        log.info("Received Order from Kafka "+ orderPlaceEvent.getOrderNumber());
    }
}
