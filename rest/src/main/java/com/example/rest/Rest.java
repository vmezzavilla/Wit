package com.example.rest;

import com.example.common.config.KafkaConfig;
import com.example.common.message.View;
import com.example.common.message.WitMessage;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;


@RestController
@SpringBootApplication
@Import(KafkaConfig.class)
public class Rest {

    private static final Logger logger = LoggerFactory.getLogger(Rest.class);

    @Value(value = "${kafka.sum-topic}")
    private String addTopic;
    @Value(value = "${kafka.sub-topic}")
    private String subTopic;
    @Value(value = "${kafka.mult-topic}")
    private String multTopic;
    @Value(value = "${kafka.div-topic}")
    private String divTopic;

    @Autowired
    private final KafkaTemplate<String, WitMessage> kafkaTemplate;
    private final ConcurrentHashMap<UUID, CompletableFuture<WitMessage>> pendingRequests = new ConcurrentHashMap<>();


    public Rest(KafkaTemplate<String, WitMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private ResponseEntity<WitMessage> sendMessage(Number n1, Number n2, String topic) throws ExecutionException, InterruptedException {
        WitMessage message = new WitMessage(UUID.randomUUID(),
                                             n1,
                                             n2,
                                             null);
        MDC.put("requestId", message.getId().toString());
        CompletableFuture<WitMessage> future = new CompletableFuture<>();
        pendingRequests.put(message.getId(), future);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Request-Id", message.getId().toString());
        logger.info(String.format("Send numbers: %f & %f, topic: %s",message.getNumber1().doubleValue(),
                                                                     message.getNumber2().doubleValue(),
                                                                     topic));
        kafkaTemplate.send(topic, message);
        MDC.remove("requestId");
        return ResponseEntity.ok()
                .headers(headers)
                .body(future.get());
    }

    @GetMapping("/sum")
    @JsonView(View.Rest.class)
    public ResponseEntity<WitMessage> sum(@RequestParam Number a, @RequestParam Number b) throws Exception {
        return sendMessage(a,b,addTopic);
    }

    @GetMapping("/subtraction")
    @JsonView(View.Rest.class)
    public ResponseEntity<WitMessage> subtraction(@RequestParam Number a, @RequestParam Number b) throws Exception {
        return sendMessage(a,b,subTopic);
    }

    @GetMapping("/multiplication")
    @JsonView(View.Rest.class)
    public ResponseEntity<WitMessage> multiplication(@RequestParam Number a, @RequestParam Number b) throws Exception {
        return sendMessage(a,b,multTopic);
    }

    @GetMapping("/division")
    @JsonView(View.Rest.class)
    public ResponseEntity<WitMessage> division(@RequestParam Number a, @RequestParam Number b) throws Exception {
        return sendMessage(a,b,divTopic);
    }

    @KafkaListener(topics = "${kafka.response-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listner(WitMessage message) {
        MDC.put("requestId", message.getId().toString());
        logger.info(String.format("Recive response result: %f",message.getResult().doubleValue()));
        CompletableFuture<WitMessage> future = pendingRequests.remove(message.getId());
        if (future != null) {
            future.complete(message);
        }
        MDC.remove("requestId");
    }

    public static void main(String[] args) {
        SpringApplication.run(Rest.class, args);
    }
}