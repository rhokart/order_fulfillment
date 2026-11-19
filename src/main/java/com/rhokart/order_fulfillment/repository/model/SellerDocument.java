package com.rhokart.order_fulfillment.repository.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "seller_detail")
public class SellerDocument {

    @Id
    private String id;
    private String sellerName;
    private String email;
    private String mobileNo;
    private BusinessDetail businessDetail;
    private SellerAddress sellerAddress;
    private List<Documents> documents;
}
