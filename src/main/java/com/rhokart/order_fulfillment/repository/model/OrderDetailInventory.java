package com.rhokart.order_fulfillment.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailInventory {
    private String skuId;
    private Integer units;
    private Integer unitPriceWithoutGst;
    private Integer unitPriceWithGst;
    private String hsnCode;
    private Integer gst;
}

