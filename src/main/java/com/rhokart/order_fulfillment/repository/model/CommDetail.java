package com.rhokart.order_fulfillment.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommDetail {
    private BigDecimal rhokartComm;
    private BigDecimal partnerComm;
}
