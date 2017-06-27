package com.moringaprods.HashTagClient;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by mithunbondugula on 6/27/17.
 * Listens to the passed in topics and prints the message
 */
@Service("HashtagConsumer")
public class HashtagConsumer {
    private String groupId;
    @Value("${kafka.broker-list}")
    private String bootStrapServers;

    private static final Logger LOGGER = LoggerFactory.getLogger(HashtagConsumer.class);

    public void listen(String[] hashTags){
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList(hashTags));

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    LOGGER.info("Message received- Offset: " + record.offset() + " Key:" + record.key() + "Message: " + record.value());
                }
            }
        } finally {
            consumer.close();
        }
    }

    public void setGroupId(String groupId){
        this.groupId = groupId;
    }
}

