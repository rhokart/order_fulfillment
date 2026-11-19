package com.rhokart.order_fulfillment.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessDetail {
    private String gstin;
    private String entityName;
    private String contactName;
    private String tradeName;
    private String country;
    private String pincode;
    private String state;
    private String city;
    private String tan;
    private String pan;
    private String phoneNo;
    private String email;
    private String altEmail;
    private String altPhoneNo;
}
