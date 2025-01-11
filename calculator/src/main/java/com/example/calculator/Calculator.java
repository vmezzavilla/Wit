package com.example.calculator;

import com.example.common.config.KafkaConfig;
import com.example.common.message.WitMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@SpringBootApplication
@Import(KafkaConfig.class)
public class Calculator {

    private static final Logger logger = LoggerFactory.getLogger(Calculator.class);

    @Value(value = "${kafka.response-topic}")
    private String responseTopic;
    @Value(value = "${kafka.sum-topic}")
    private String addTopic;
    @Value(value = "${kafka.sub-topic}")
    private String subTopic;
    @Value(value = "${kafka.mult-topic}")
    private String multTopic;
    @Value(value = "${kafka.div-topic}")
    private String divTopic;

    @Autowired
    protected final KafkaTemplate<String, WitMessage> kafkaTemplate;

    public Calculator(KafkaTemplate<String, WitMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private void processOperation(WitMessage message, String topic, Operation operation) {
        MDC.put("requestId", message.getId().toString());
        logger.info(String.format("start topic: %s", topic));
        logger.info(String.format("Recived Numbers: %f & %f", message.getNumber1().doubleValue(),
                                                              message.getNumber2().doubleValue()));

        message.setResult(operation.apply(message.getNumber1().doubleValue(), message.getNumber2().doubleValue()));

        logger.info(String.format("topic: %s, number 1: %f, number 2: %f, result: %f", topic,
                                                                                    message.getNumber1().doubleValue(),
                                                                                    message.getNumber2().doubleValue(),
                                                                                    message.getResult().doubleValue()));

        kafkaTemplate.send(responseTopic, message);
        logger.info(String.format("end topic: %s", topic));
        MDC.remove("requestId");
    }

    @KafkaListener(topics = "${kafka.sum-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void sum(WitMessage message) {
        processOperation(message, addTopic, (n1, n2) -> n1 + n2);
    }

    @KafkaListener(topics = "${kafka.sub-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void subtract(WitMessage message) {
        processOperation(message, subTopic, (n1, n2) -> n1 - n2);
    }

    @KafkaListener(topics = "${kafka.mult-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void multiply(WitMessage message) {
        processOperation(message, multTopic, (n1, n2) -> n1 * n2);
    }

    @KafkaListener(topics = "${kafka.div-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void divide(WitMessage message) {
        processOperation(message, divTopic, (n1, n2) -> n1 / n2);
    }

    @FunctionalInterface
    interface Operation {
        double apply(double n1, double n2);
    }

    public static void main(String[] args) {
        SpringApplication.run(Calculator.class, args);
    }
}
