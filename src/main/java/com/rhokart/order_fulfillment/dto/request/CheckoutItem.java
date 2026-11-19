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
public class CheckoutItem {
    private Integer cartId;
    private Integer orderAmountIncludesGst;
    private Integer orderAmountExcludesGst;
    private Integer gstAmount;
    private Integer discount;
    private Integer deliveryCharges;
    private Integer offerAmount;
    private Integer payableAmount;
    private String couponCode;
    private String sellerId;
    private String sellerName;
    private AddressDetail deliveryAddress;
    private List<Sku> skus;
}
