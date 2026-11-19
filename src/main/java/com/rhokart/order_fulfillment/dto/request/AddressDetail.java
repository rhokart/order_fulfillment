package com.rhokart.order_fulfillment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDetail {
    private String addressId;
    private String userId;
    private String fname;
    private String lname;
    private String addressLine1;
    private String addrNickname;
    private String addressLine2;
    private String city;
    private String pincode;
    private String state;
    private String country;
    private String contactNo;
    private String landmark;
    private String addressType;
}
