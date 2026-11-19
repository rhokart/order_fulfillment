package com.rhokart.order_fulfillment.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartInfo {
    private Integer cartId;
    private String userId;
    private String skuId;
    private String skuName;
    private Integer quantity;
    private String sellerId;
    private String sellerName;
    private String listingId;
    private String listingName;
    private String status;
    private String imgUrl;
    private String insertBy;
    private String updateBy;
    private Boolean isOrderPlace;
}
