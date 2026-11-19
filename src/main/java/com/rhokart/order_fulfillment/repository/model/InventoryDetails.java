package com.rhokart.order_fulfillment.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryDetails {
    private String skuId;
    private String skuName;
    private String sellerId;
    private String sellerName;
    private Integer units;
    private Integer unitPriceWithoutGst;
    private Integer unitPriceWithGst;
    private Integer unitPriceWithGstWithCommission;
    private Integer unitPriceWithoutGstWithCommission;
    private Integer gst;
    private boolean isActive;
    private String currency;
}

