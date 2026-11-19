package com.rhokart.order_fulfillment.repository.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetail {
    private String detailId;
    private String orderId;
    private String skuId;
    private String skuName;
    private String listingId;
    private String listingName;
    private Integer quantity;
    private Integer pckdQty;
    private Integer totalAmount;
    private Integer rhokartComm;
    private Integer rhokartCommWithGst;
    private Integer rhopartnerComm;
    private Integer unitPriceWoGst;
    private Integer unitPriceWithGst;
    private BigDecimal gstPct;
    private BigDecimal rhokartPct;
    private String insertBy;
    private LocalDateTime insertTimestamp;
    private String updateBy;
    private LocalDateTime updateTimestamp;
}
