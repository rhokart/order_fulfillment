package com.rhokart.order_fulfillment.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDetail {

    private String paymentId;
    private Integer amount;
    private String currency;
    private String status;
    private String pgMethod;
    private Boolean isCapture;
    private String contact;
    private String email;
    private Integer fee;
    private Integer tax;
    private String pgDump;
}
