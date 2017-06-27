package com.moringaprods.HashTagClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Created by mithunbondugula on 6/27/17.
 *Consumer client for listening to twts on the tags
 */
public class HashTagClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(HashTagClient.class);

    public static void main(String[] args) throws Exception {
        if(args.length == 0){
            LOGGER.error("No Hashtags to receive! Pick some please.");
            return;
        }
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        HashtagConsumer consumer = (HashtagConsumer) context.getBean(HashtagConsumer.class);
        //setting a unique group id for each combination of Hashtags
        //consumers that read this combination will be in same group
        consumer.setGroupId(String.join("-", args));
        consumer.listen(args);
    }
}