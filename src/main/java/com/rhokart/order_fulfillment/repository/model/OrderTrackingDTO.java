package com.rhokart.order_fulfillment.repository.model;

import lombok.Data;

@Data
public class OrderTrackingDTO {
    private Long trkId;
    private String ordId;
    private Long addrId;
    private String trackNbr;
    private String shippingMethod;
    private String carrier;
    private String status;
    private String estDeliveryDte;
    private String deliveredAt;
    private Integer attemptCount;
    private Boolean isSignature;
    private String signatureBy;
    private String expCde;
    private String insertBy;
    private String insertTmstp;
    private String updateBy;
    private String updateTmstp;
    private String userId;
    private String addrLine1;
    private String addrLine2;
    private String city;
    private String pincode;
    private String country;
    private String contactNo;
    private String landmark;
    private Boolean isActive;
    private String addrType;
    private String fname;
    private String lname;
    private String state;
    private String otp;

}

