package com.moringaprods.TwitterProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import twitter4j.*;

/**
 * Created by mithunbondugula on 6/25/17.
 * Listener based Twitter stream consumer and then passed on to SenderService for persisting the tweets
 */
@Service("TwitterStreamReceiver")
public class TwitterStreamReceiver {
    SenderService sender;
    private String[] hashTagTopics;
    TwitterStream twitterStream;
    FilterQuery query;

    // set this to true to stop the thread
    volatile boolean shutdown = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterStreamReceiver.class);

    public void run(){
        twitterStream = new TwitterStreamFactory().getInstance();
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                LOGGER.info("@" + status.getUser().getScreenName() + " - " + status.getText() + status.getCreatedAt());
                //TODO: check if key needs to be passed
                sender.send(null, (status.getUser().getScreenName() + "," + status.getText()  + "," + status.getCreatedAt()));
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                LOGGER.info("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                LOGGER.info("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                LOGGER.info("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                LOGGER.info("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                //close the producer in case of an error in the lister
                LOGGER.error(ex.toString());
            }
        };
        // Spawns a new thread and listens for activity on the hashTagTopic
        twitterStream.addListener(listener);
        query = new FilterQuery(hashTagTopics);
        twitterStream.filter(query);
    }

    public void stop(){
        //sets the interrupt flag
        this.shutdown = true;
    }

    public void setSender(SenderService sender){
        this.sender = sender;
    }

    public void setHashtagTopics(String[] hashTagTopics){
        this.hashTagTopics = hashTagTopics;
    }
}
