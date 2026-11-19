package com.rhokart.order_fulfillment.repository;


import com.rhokart.order_fulfillment.repository.model.ListingDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ListingRepo extends MongoRepository<ListingDetail, String> {
    Optional<ListingDetail> findById(String id);

}
