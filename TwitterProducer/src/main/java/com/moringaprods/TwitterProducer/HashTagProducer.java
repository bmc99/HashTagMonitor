package com.moringaprods.TwitterProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Created by mithunbondugula on 6/24/17.
 */
/**
* Use TwitterStreamReceiver and SenderService to receive tweets and then send them to configure topic
 */
public class HashTagProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(HashTagProducer.class);

    public static void main(String[] args) {
        //Load context in which the Beans run
        AbstractApplicationContext  context = new AnnotationConfigApplicationContext(AppConfig.class);
        AbstractApplicationContext contextTwitter = new ClassPathXmlApplicationContext("Beans.xml");

        if(args.length == 0){
            LOGGER.error("No Hashtags to monitor! Pick some please.");
            return;
        }

        //initate the Kafka sender so that its ready when the tweets start flowing
        //sender puts all tweets in one configured Topic
        SenderService senderService = (SenderService) context.getBean("KafkaSenderService");
        senderService.initiateProducer();

        //start the twitter stream to monitor passed in tags
        TwitterStreamReceiver twtReceiver = (TwitterStreamReceiver) contextTwitter.getBean("TwitterStreamReceiver");
        twtReceiver.setHashtagTopics(args);
        twtReceiver.setSender(senderService);
        twtReceiver.run();

    }
}