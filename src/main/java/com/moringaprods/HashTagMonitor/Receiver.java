package com.moringaprods.HashTagMonitor;

/**
 * Created by mithunbondugula on 6/24/17.
 */
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

public class Receiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    private CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {
        return latch;
    }

    @KafkaListener(topics = "${kafka.topic.helloworld}")
    public void receive(String message) {
        LOGGER.info("received message='{}'", message);
        latch.countDown();
    }
}
