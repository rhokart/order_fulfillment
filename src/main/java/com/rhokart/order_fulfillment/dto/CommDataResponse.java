package com.rhokart.order_fulfillment.dto;

import com.rhokart.order_fulfillment.repository.model.CommDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommDataResponse {
    private Integer catgId;
    private String brandName;
    private Integer tier;
    private CommDetail comm;
}
