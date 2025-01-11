package com.example.calculator;

import com.example.common.config.KafkaConfig;
import com.example.common.message.WitMessage;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    protected final KafkaTemplate<String, WitMessage> kafkaTemplate;

    public Calculator(KafkaTemplate<String, WitMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "sum-topic", groupId = "calculator-group")
    public void sum(WitMessage message) {
        message.setResult(message.getNumber1().add(message.getNumber2()));
        kafkaTemplate.send("response-topic", message);
    }

    @KafkaListener(topics = "sub-topic", groupId = "calculator-group")
    public void subtract(WitMessage message) {
        message.setResult(message.getNumber1().subtract(message.getNumber2()));
        kafkaTemplate.send("response-topic", message);
    }

    @KafkaListener(topics = "mult-topic", groupId = "calculator-group")
    public void multiply(WitMessage message) {
        message.setResult(message.getNumber1().multiply(message.getNumber2()));
        kafkaTemplate.send("response-topic", message);
    }

    @KafkaListener(topics = "div-topic", groupId = "calculator-group")
    public void divide(WitMessage message) {
        message.setResult(message.getNumber1().divide(message.getNumber2()));
        kafkaTemplate.send("response-topic", message);
    }

    public static void main(String[] args) {
        SpringApplication.run(Calculator.class, args);
    }
}
