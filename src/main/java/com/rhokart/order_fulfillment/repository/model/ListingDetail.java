package com.rhokart.order_fulfillment.repository.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document(collection = "listing_details")
public class ListingDetail {
    @Id
    private String id;

    private String brandName;
    private String lst_image;
    private Map<String, String> attributes;
    private Integer catg_id;
    private String listing_name;
}
