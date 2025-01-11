package com.example.rest;

import com.example.common.config.KafkaConfig;
import com.example.common.message.WitMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
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

    private WitMessage sendMessage(Number n1, Number n2, String topic) throws ExecutionException, InterruptedException {
         WitMessage message = new WitMessage(UUID.randomUUID(),
                                             BigDecimal.valueOf(n1.doubleValue()),
                                             BigDecimal.valueOf(n2.doubleValue()),
                                             null);

        CompletableFuture<WitMessage> future = new CompletableFuture<>();
        pendingRequests.put(message.getId(), future);
        kafkaTemplate.send(topic, message);
        return future.get();
    }

    @GetMapping("/sum")
    public Response sum(@RequestParam Number a, @RequestParam Number b) throws Exception {
        return new Response(sendMessage(a,b,addTopic).getResult());
    }

    @GetMapping("/subtraction")
    public Response subtraction(@RequestParam Number a, @RequestParam Number b) throws Exception {
        return new Response(sendMessage(a,b,subTopic).getResult());
    }

    @GetMapping("/multiplication")
    public Response multiplication(@RequestParam Number a, @RequestParam Number b) throws Exception {
        return new Response(sendMessage(a,b,multTopic).getResult());
    }

    @GetMapping("/division")
    public Response division(@RequestParam Number a, @RequestParam Number b) throws Exception {
        return new Response(sendMessage(a,b,divTopic).getResult());
    }

    @KafkaListener(topics = "response-topic", groupId = "calculator-group")
    public void listner(WitMessage message) {
        CompletableFuture<WitMessage> future = pendingRequests.remove(message.getId());
        if (future != null) {
            future.complete(message);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Rest.class, args);
    }
}