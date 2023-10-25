package io.arete.kafka_sample;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class Main {
    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.put("bootstrap.servers","localhost:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(properties);
        int count = 0;
        for(int i=2; i< 100; i++) {
            boolean isPrime = true;
            for(int j=i-1; j>1; j--) {
                if (i%j == 0) {
                    isPrime =  false;
                    break;
                }
            }
            if (isPrime)
                producer.send(new ProducerRecord<>("my-topic","prime_" + ++count, String.valueOf(i)));
        }
        producer.close();
    }
}