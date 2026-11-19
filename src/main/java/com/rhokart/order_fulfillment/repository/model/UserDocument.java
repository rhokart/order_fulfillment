package com.rhokart.order_fulfillment.repository.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "user_mgmt")
public class UserDocument {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String primaryMob;
    private String secondaryMob;
    private UserAddress userAddress;
    private UserBusinessDetail businessDetail;
}