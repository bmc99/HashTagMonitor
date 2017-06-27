package com.moringaprods.TwitterProducer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Created by mithunbondugula on 6/26/17.
 * An implementation of SenderServer that sends to Kafka Topic
 */
@Service("KafkaSenderService")
public class KafkaSenderService implements SenderService {
    Properties props;
    Producer<String, String> producer;

    @Value("${kafka.key.serializer.class}")
    private String keySerializer;
    @Value("${kafka.value.serializer.class}")
    private String valueSerializer;
    @Value("${kafka.broker-list}")
    private String bootStrapServers;
    @Value("${kafka.AllTagTopics}")
    private String hashTagTopic;

    private static final Logger LOGGER = LoggerFactory.getLogger(HashTagProducer.class);


    public void initiateProducer() {
        props = new Properties();
        props.put("bootstrap.servers", bootStrapServers);
        props.put("key.serializer", keySerializer);
        props.put("value.serializer", valueSerializer);

        producer = new KafkaProducer<String, String>(props);
    }

    public void send(String key, String message) {
        if (this.producer != null) {
            producer.send(new ProducerRecord<String, String>(hashTagTopic, null, message));
        } else {
            LOGGER.debug("Send invoked before producer is instantiated");
        }
    }

    public void close() {
        if (this.producer != null) {
            this.producer.close();

        } else {
            LOGGER.debug("destroy invoked before producer is instantiated");
        }
    }
}
