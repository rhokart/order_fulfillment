package com.rhokart.order_fulfillment.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private String addressLine1;
    private String addressLine2;
    private String pincode;
    private String city;
    private String district;
    private String state;
    private String country;
    private String landmark;
}
