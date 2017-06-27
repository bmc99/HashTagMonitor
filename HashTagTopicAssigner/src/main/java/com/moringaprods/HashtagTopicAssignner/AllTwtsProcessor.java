package com.moringaprods.HashtagTopicAssignner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Created by mithunbondugula on 6/26/17.
 */
public class AllTwtsProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AllTwtsProcessor.class);

    public static void main(String[] args) throws Exception {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        if(args.length == 0){
            LOGGER.error("No Hashtags to parse! Pick some please.");
            return;
        }

        //invoke the analyzer
        AllHashtagsAnalyzer analyzer = (AllHashtagsAnalyzer) context.getBean("AllHashtagsAnalyzer");
        analyzer.analyze(args);
    }
}
