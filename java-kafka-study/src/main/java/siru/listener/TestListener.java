package siru.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
@RequiredArgsConstructor
public class TestListener {

    private final CountDownLatch latch = new CountDownLatch(1);

    @KafkaListener(topics = "example_topic1")
    public void exampleTopicListener(String kafkaMessage) {
        log.info("Kafka Message: -> {}", kafkaMessage);

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        log.info("data: {}", map);
    }

    @KafkaListener(topics = "test-topic1")
    public void updateQty(String message) {
        log.info("Kafka Message: -> {}", message);
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}