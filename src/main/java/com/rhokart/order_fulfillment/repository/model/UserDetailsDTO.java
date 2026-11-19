package com.rhokart.order_fulfillment.repository.model;

import lombok.Data;

@Data
public class UserDetailsDTO {
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String primaryMob;
    private String secondaryMob;
    private boolean isProfileCompleted;
    private UserAddress userAddress;
    private UserBusinessDetail businessDetail;


}

