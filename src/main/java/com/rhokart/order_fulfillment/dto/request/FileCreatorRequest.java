package com.rhokart.order_fulfillment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileCreatorRequest {
    private String sellerId;
    private String orderId;
}
