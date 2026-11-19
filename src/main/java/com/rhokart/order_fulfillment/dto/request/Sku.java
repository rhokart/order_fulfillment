package com.rhokart.order_fulfillment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sku {
    private String listingId;
    private String listingName;
    private String skuId;
    private String skuName;
    private Integer totalQuantity;
    private BigDecimal gstPct;
    private Integer totalAmountWoGst;
    private Integer totalAmount;
}
