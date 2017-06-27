package com.moringaprods.TwitterProducer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Created by mithunbondugula on 6/26/17.
 * An implementation of SenderServer that sends to Kafka Topic
 */
@Service("KafkaSenderService")
public class KafkaSenderService implements SenderService{
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

    public void initiateProducer(){
        props = new Properties();
        props.put("bootstrap.servers", bootStrapServers);
        props.put("key.serializer", keySerializer);
        props.put("value.serializer", valueSerializer);

        producer = new KafkaProducer<String, String>(props);
    }

    public void send(String key, String message){
        producer.send(new ProducerRecord<String, String>(hashTagTopic, null, message));
    }

    public void destroy(){
        if(this.producer != null)
            this.producer.close();
    }
}
