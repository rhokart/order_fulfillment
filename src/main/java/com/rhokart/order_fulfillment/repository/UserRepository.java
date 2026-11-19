package com.rhokart.order_fulfillment.repository;


import com.rhokart.order_fulfillment.repository.model.UserDetailsDTO;
import com.rhokart.order_fulfillment.repository.model.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserDocument, String> {

    @Query(value = "{ '_id': ?0 }", fields = "{ 'firstName': 1, 'lastName': 1, 'gender': 1, 'email': 1, 'isProfileCompleted':1, 'primaryMob': 1, 'secondaryMob': 1, 'userAddress': 1, 'businessDetail': 1 }")
    UserDetailsDTO findUserDetailsById(String userId);
}
