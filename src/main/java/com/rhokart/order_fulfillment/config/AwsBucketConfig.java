package com.rhokart.order_fulfillment.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;


@Configuration
public class AwsBucketConfig {

    @Value("${spring.aws.s3bucket.access.key}")
    private String awsAccessKey;

    @Value("${spring.aws.s3bucket.secret.key}")
    private String awsSecretKey;

    @Value("${spring.aws.s3bucket.region.static}")
    private String awsRegion;


    @Bean
    public S3Client s3Client() {
        try {
            return S3Client.builder()
                    .region(awsRegion != null ? Region.of(awsRegion) :
                            DefaultAwsRegionProviderChain.builder().build().getRegion())
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(awsAccessKey, awsSecretKey)
                    ))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsAccessKey, awsSecretKey)
                ))
                .build();
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "|{}[]"));
        return factory;
    }

}
