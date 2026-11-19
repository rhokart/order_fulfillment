package com.rhokart.order_fulfillment.service;


import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
public class SqsMessageProducer {

    private final SqsAsyncClient sqsAsyncClient;

    public SqsMessageProducer(SqsAsyncClient sqsAsyncClient) {
        this.sqsAsyncClient = sqsAsyncClient;
    }

    public CompletableFuture<String> sendMessage(String queueUrl, String messageBody) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .messageGroupId("order-fulfillment")
                .messageDeduplicationId(String.valueOf(Instant.now().getEpochSecond()))
                .build();

        return sqsAsyncClient.sendMessage(request)
                .thenApply(response -> response.messageId());
    }
}

