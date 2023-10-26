package io.arete.sample.kafka.streams;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;


import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

public class StreamsMain {
    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,"number-process");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_DOC, 0);
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.String().getClass().getName());

        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> source = builder.stream("number-input-topic");
        source.to("number-out-topic");

        final Topology topology = builder.build();

        final KafkaStreams streams = new KafkaStreams(topology, properties);

        System.out.println(topology.describe());

        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("number-process-shutdownHook"){
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (InterruptedException e) {
            streams.close();
            System.exit(1);
        }
        System.exit(0);
    }
}