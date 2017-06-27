package com.moringaprods.HashtagTopicAssignner;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Created by mithunbondugula on 6/26/17.
 * Picks up tweets from all tags topic and moves the tweets to their respective topics based on existance of hashtag
 * A tweet can contain multiple hashtags - hence uses stream filter rather than stream branch
 */
@Service("AllHashtagsAnalyzer")
public class AllHashtagsAnalyzer {
    @Value("${kafka.analyzer.appId}")
    private String appId;
    @Value("${kafka.broker-list}")
    private String bootStrapServers;
    @Value("${kafka.AllTagTopics}")
    private String hashTagTopic;

    KafkaStreams streams;
    private static final Logger LOGGER = LoggerFactory.getLogger(AllHashtagsAnalyzer.class);


    public void analyze(String[] tagsToAnalyze){
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        try {
            KStreamBuilder builder = new KStreamBuilder();
            KStream<String, String> sourceStream = builder.stream(hashTagTopic);
            KStream<String, String> tagStream;
            for (String tag : tagsToAnalyze) {
                tagStream = sourceStream.filter((key, value) -> value.contains(tag));
                tagStream.to(tag);
            }

            streams = new KafkaStreams(builder, props);
            //Start and let it run as long as there are messages
            streams.start();
            //close streams gracefully in case of an uncaught exception
            streams.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                public void uncaughtException(Thread t, Throwable e) {
                    streams.close();
                    LOGGER.error(t + " thread throws exception", e);
                }
            });
        }catch (IllegalStateException e){
            //Dont do anything as the stream has already started
            LOGGER.debug("tried to restart an already started stream");
        }
        catch (Exception e){
            LOGGER.error("error while processing all tweets and moving them to specific topics", e);
            streams.close();
        }
    }

    public void close(){
        if (streams!=null){
            streams.close();
        } else{
            LOGGER.debug("tried to close a null stream");
        }
    }


}
