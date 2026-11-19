package com.rhokart.order_fulfillment.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderTracking {
    private String trackingId;
    private String orderId;
    private String addressId;
    private String trackingNumber;
    private String shippingMethod;
    private String carrier;
    private String status;
    private Date estimatedDeliveryDate;
    private LocalDateTime deliveredAt;
    private Integer attemptCount;
    private Boolean isSignature;
    private String signatureBy;
    private String exceptionCode;
    private String insertBy;
    private LocalDateTime insertAt;
    private String updateBy;
    private LocalDateTime updateAt;

}
