package com.rhokart.order_fulfillment.repository.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderMaster {
    private String masterOrderId;
    private String userId;
    private Integer totalAmount;
    private String status;
    private String payStatus;
    private String payId;
    private String insertBy;
    private String updateBy;
}
