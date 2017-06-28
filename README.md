## Synopsis
A set of micro services built on Kafka(Producer, Stream processor and Consumer) to monitor tweets for provided hashtags.

## Design considerations
Rather than build a single monolithic application, this data pipeline is built using 3 micro-services (with assumption that some of the Hashtags will have very high (or exponential) volume). Breaking the application into these parts increases the scalability, maintainability (for example, one of the services can be replaced with a better implementation and without impacting the other two) and resiliency :
1) TwitterProducer (Aka HashTagProducer): This micro service uses Twitter streaming API to source the Tweets. The service spawns a thread to listen (non-blocking) to streaming API for the filtered tweets that contain desired Hashtags. This design maintains the responsibility of filtering the messages at the source - there by eliminating a lot of noise. Received tweets are placed in a Kafka topic without any processing or further filtering. It is possible to run more than one producer to source more Hashtags - static load management.
2) HashTagTopicAssigner: Uses Kafka stream processing to assign the tweets to Hashtag topics - this is done per tweet as they come in. Each Hashtag will have its own Kafka topic - allowing for clients to subscribe to 1 to many Hashtags in any combination. Multiple instances of the service can be run to better balance the load. 
3) HashTagClient: A client can subscribe to any number of Hashtags. Multiple clients can be spawned at a give time. A client can register and de-register to Hashtags. Created datetime of the tweet can be used to maintain tweet order when a client group is subscribed to a topic with multiple partitions.

## Installation (for Linux box)
### Pre-requisites:
zookeeper-3.4.6 and kafka_2.11-0.10.2.1 should be setup and running.

### Zookeeper
Zookeeper 3.4.6 can be downloaded from Apache ZooKeeper Releases

From desired directory for zookeeper:

    wget http://apache.mirrors.lucidnetworks.net/zookeeper/zookeeper-3.4.6/zookeeper-3.4.6.tar.gz
    tar -xvf zookeeper-3.4.6.tar.gz
    cd zookeeper-3.4.6/

Start:

    cp conf/zoo_sample.cfg conf/zoo.cfg
    bin/zkServer.sh start

Expected message:

    JMX enabled by default
    Using config: /home/ubuntu/zookeeper-3.4.6/bin/../conf/zoo.cfg
    Starting zookeeper ... STARTED

### Kafka
Kafka 0.10.2.1 can be downloaded from Apache Kafka Releases

From desired directory for kafka:

    wget http://apache.claz.org/kafka/0.10.2.1/kafka_2.11-0.10.2.1.tgz
    tar xvzf kafka_2.11-0.10.2.1.tgz  

Start:

    cd kafka_2.11-0.10.0.0
    bin/kafka-server-start.sh config/server.propertie


#### Setup the HashTagMonitor repo
Clone or fork the repo

     git clone git@github.com:bmc99/HashTagMonitor    
     cd HashTagMonitor

#### Add twitter keys

#### Twitter app registration 

Follow the instructions on https://apps.twitter.com/ to create a new twitter app

Create and make a copy of Keys and Access Tokens.

#### Update properties

Navigate to twitter4j.properties file @ /TwitterProducer/src/main/resources/

Add appropriate values to following properties:

oauth.consumerKey

oauth.consumerSecre

oauth.accessToken

oauth.accessTokenSecret

#### package:
   
    mvn clean package

#### Running the services:
Assuming that services are being run for following tags: iHeartAwards BestFanArmy Harmonizers Directioners 5SOSFam KCA gameinsight android androidgames GOT7

Assigner - from the HashTagTopicAssigner folder - service starts monitoring the all tweets topic:
    
    mvn exec:java -Dexec.args="iHeartAwards BestFanArmy Harmonizers Directioners 5SOSFam KCA gameinsight android androidgames GOT7"
      

Client - from the HashTagClient folder - service starts monitoring the tweets in each of the specific topics:
      
      mvn exec:java -Dexec.args="iHeartAwards BestFanArmy Harmonizers Directioners 5SOSFam KCA gameinsight android androidgames GOT7"

Producer - from the TwitterProducer folder - service sources tweets and places them in all tweets topic :
      
      mvn exec:java -Dexec.args="iHeartAwards BestFanArmy Harmonizers Directioners 5SOSFam KCA gameinsight android androidgames GOT7"

### Enchancements
1) Order across partitions: Created datatime stamped at the source can be used to maintain the order of the messages. A custom timestamp extractors, for instance to retrieve timestamps embedded in the payload of messages can be used like below:

       import java.util.Properties;
       import org.apache.kafka.streams.StreamsConfig;

       Properties streamsConfiguration = new Properties();
       streamsConfiguration.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, MyEventTimeExtractor.class);

2) Custom serializer and deserializer can be built to get a lot of tweet attributes not just the status (message)

3) Client can be either be extended to write to varied data sources or exposed as REST API to support more subscribers


