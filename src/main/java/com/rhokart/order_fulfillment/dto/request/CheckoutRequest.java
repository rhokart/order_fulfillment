package com.rhokart.order_fulfillment.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutRequest {
    private String UserId;
    private Integer totalPayableAmount;
    private List<CheckoutItem> checkoutItems;
}
