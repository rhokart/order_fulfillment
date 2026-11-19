package com.rhokart.order_fulfillment.service.helper;

import com.rhokart.order_fulfillment.dto.request.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    private final S3Client s3Client;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${application.aws.s3.bucket.name}")
    String bucketName;

    public EmailService(JavaMailSender mailSender, S3Client s3Client) {
        this.mailSender = mailSender;
        this.s3Client = s3Client;
    }

    public void sendEmailWithAttachement(EmailDetails emailDetails) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(emailDetails.getToAddress());
        helper.setSubject(emailDetails.getSubject());
        helper.setText(emailDetails.getMsgBody());
        helper.setFrom(sender);

        // Attach PDF from S3
        attachPdfFromS3(helper, bucketName, emailDetails.getFilename(), "Invoice.pdf");

        mailSender.send(message);
    }


    public boolean sendEmailToUser(EmailDetails emailDetails) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(emailDetails.getToAddress());
            simpleMailMessage.setSubject(emailDetails.getSubject());
            simpleMailMessage.setText(emailDetails.getMsgBody());
            simpleMailMessage.setFrom(sender);

            mailSender.send(simpleMailMessage);

        } catch (Exception ex) {
            log.error("Error while sending email.");
            log.info("message:{}", ex.fillInStackTrace());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public void attachPdfFromS3(
            MimeMessageHelper helper,
            String bucketName,
            String key,
            String attachmentName
    ) throws IOException, MessagingException {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
            byte[] content = IOUtils.toByteArray(s3Object);
            ByteArrayResource attachment = new ByteArrayResource(content);
            helper.addAttachment(attachmentName, attachment);
        }
    }
}
