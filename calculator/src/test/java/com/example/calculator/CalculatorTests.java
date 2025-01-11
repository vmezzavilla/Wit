package com.example.calculator;

import com.example.common.config.KafkaConfig;
import com.example.common.message.WitMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;
import java.util.concurrent.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import(KafkaConfig.class)
class CalculatorTests {

	@Autowired
	private KafkaTemplate<String, WitMessage> kafkaTemplate;
	private final ConcurrentHashMap<UUID, CompletableFuture<WitMessage>> pendingRequests = new ConcurrentHashMap<>();

	private WitMessage sendMessage(Number n1, Number n2, String topic) throws ExecutionException, InterruptedException, TimeoutException {
		WitMessage message = new WitMessage(UUID.randomUUID(),
				n1,
				n2,
				null);

		CompletableFuture<WitMessage> future = new CompletableFuture<>();
		pendingRequests.put(message.getId(), future);
		kafkaTemplate.send(topic, message);
		return future.get(10, TimeUnit.MILLISECONDS);
	}

	@Test
	@DirtiesContext
	public void testSumMessage() throws ExecutionException, InterruptedException, TimeoutException {
		WitMessage responseMessage = sendMessage(8,2,"sum-topic");
		Assertions.assertNotNull(responseMessage);
		Assertions.assertEquals(responseMessage.getResult().doubleValue(),10);
	}

	@Test
	public void testSubMessage() throws ExecutionException, InterruptedException, TimeoutException {
		WitMessage responseMessage = sendMessage(8,2,"sub-topic");
		Assertions.assertNotNull(responseMessage);
		Assertions.assertEquals(responseMessage.getResult().doubleValue(),6);
	}

	@Test
	public void testMultMessage() throws ExecutionException, InterruptedException, TimeoutException {
		WitMessage responseMessage = sendMessage(8,2,"mult-topic");
		Assertions.assertNotNull(responseMessage);
		Assertions.assertEquals(responseMessage.getResult().doubleValue(),16);
	}

	@Test
	public void testDivMessage() throws ExecutionException, InterruptedException, TimeoutException {
		WitMessage responseMessage = sendMessage(8,2,"div-topic");
		Assertions.assertNotNull(responseMessage);
		Assertions.assertEquals(responseMessage.getResult().doubleValue(),4);
	}
}
