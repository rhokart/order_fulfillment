package com.rhokart.order_fulfillment.repository.model;

import lombok.Data;

@Data
public class UserAddress {
    private String addressLine1;
    private String addressLine2;
    private String pincode;
    private String city;
    private String district;
    private String state;
}