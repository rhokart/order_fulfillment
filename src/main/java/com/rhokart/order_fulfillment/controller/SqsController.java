package com.rhokart.order_fulfillment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhokart.datamodel.dto.CoreData;
import com.rhokart.datamodel.dto.FailureInfo;
import com.rhokart.datamodel.dto.Response;
import com.rhokart.datamodel.dto.SuccessInfo;
import com.rhokart.datamodel.util.Constants;
import com.rhokart.order_fulfillment.dto.request.FileCreatorRequest;
import com.rhokart.order_fulfillment.dto.response.DownloadInvoiceResponse;
import com.rhokart.order_fulfillment.repository.TransactionRepo;
import com.rhokart.order_fulfillment.service.SqsMessageProducer;
import com.rhokart.order_fulfillment.service.helper.EmailService;
import com.rhokart.order_fulfillment.service.helper.FileCreator;
import com.rhokart.order_fulfillment.service.helper.SellerFileCreator;
import com.rhokart.order_fulfillment.util.GlobalConstant;
import com.rhokart.order_fulfillment.util.GlobalValidator;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class SqsController {

    @Autowired
    private ObjectMapper objectMapper;
    @Value("${application.aws.s3.bucket.name}")
    String bucketName;
    private final S3Presigner s3Presigner;

    private final SqsMessageProducer producer;

    private final FileCreator fileCreator;
    private final SellerFileCreator sellerFileCreator;
    private final EmailService emailService;
    private final TransactionRepo transactionRepo;

    // Inject the queue URL from application.yml
    @Value("${aws.sqs.queueUrl}")
    private String queueUrl;

    public SqsController(S3Presigner s3Presigner, SqsMessageProducer producer, FileCreator fileCreator, SellerFileCreator sellerFileCreator, EmailService emailService, TransactionRepo transactionRepo) {
        this.s3Presigner = s3Presigner;
        this.producer = producer;
        this.fileCreator = fileCreator;
        this.sellerFileCreator = sellerFileCreator;
        this.emailService = emailService;
        this.transactionRepo = transactionRepo;
    }


    // POST endpoint to send a message
    @PostMapping("/send")
    @SneakyThrows
    public ResponseEntity<String> sendMessage(@RequestBody FileCreatorRequest messageBody) {
        String messageId = producer.sendMessage(queueUrl, objectMapper.writeValueAsString(messageBody)).join();
        return ResponseEntity.ok("Message sent. ID: " + messageId);
    }


    @SqsListener("order-processing.fifo")
    public void processOrder(String message) {
        log.info("Raw SQS message received: {}", message);
        try {
            FileCreatorRequest fileCreatorRequest = objectMapper.readValue(message, FileCreatorRequest.class);
            log.info("Deserialized FileCreator: {}", fileCreatorRequest);
            // Proceed with processing
//            transactionRepo.updateOrderTrackingWithOtp(GlobalValidator.generateOtp(), fileCreatorRequest.getOrderId() ,LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")));
//            fileCreator.createPdf(fileCreatorRequest);
            sellerFileCreator.createPdf(fileCreatorRequest);
            log.info("Invoice pdf is created...");
            transactionRepo.updateOrderInfoWithSellerInvId(fileCreatorRequest.getOrderId(), "RHOSEL".concat(fileCreatorRequest.getOrderId()));
        } catch (Exception e) {
            log.error("Failed to deserialize message to FileCreator", e);
        }
    }

    @PostMapping("/download/invoice")
    @SneakyThrows
    public ResponseEntity<DownloadInvoiceResponse> downloadInvoice(@RequestBody FileCreatorRequest fileCreatorRequest) {
        String filename = fileCreator.createPdf(fileCreatorRequest);

        String key = filename;

        try {
            // Set expiration for 1 hour from now
            Instant expiration = Instant.now().plusSeconds(3600);

            // Create a GetObjectRequest
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            // Generate the presigned URL using the injected bean
            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(r -> r
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(java.time.Duration.ofHours(1)));

            URL url = presignedGetObjectRequest.url();

            return ResponseEntity.ok(DownloadInvoiceResponse.builder().invoiceUrl(url.toString()).build());

        } catch (S3Exception e) {
            return ResponseEntity.ok(DownloadInvoiceResponse.builder().invoiceUrl("").build());
        }
    }

}
