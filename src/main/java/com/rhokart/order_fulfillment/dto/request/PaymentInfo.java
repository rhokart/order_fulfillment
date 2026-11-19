package com.rhokart.order_fulfillment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInfo {
    private String paymentId;
    private Integer paidAmount;
    private String currency;
    private String status;
    private String paymentMethod;
    private boolean capture;
}
