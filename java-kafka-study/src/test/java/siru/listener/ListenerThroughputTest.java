package siru.listener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class ListenerThroughputTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private TestListener testListener;

    @Test
    void listenerThroughputTest() {
        System.out.println("###");
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("test-topic1","test");
        try {
            SendResult<String, String> result = future.get();
            System.out.println("#### " + result );
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try {
            testListener.getLatch().await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("시간초과");
        }

        assertThat(testListener.getLatch().getCount()).isEqualTo(0);
    }
}
