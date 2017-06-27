package com.moringaprods.TwitterProducer;

/**
 * Created by mithunbondugula on 6/26/17.
 * Allows for implementation of different SenderService that abide by below contract
 */
public interface SenderService {
    void initiateProducer();
    void send(String key, String message);
    void destroy();
}
