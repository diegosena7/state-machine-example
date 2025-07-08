package com.dsena7.repository;

import com.dsena7.model.ConsentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ConsentRepository extends MongoRepository<ConsentEntity, String> {
    @Query("{ 'consentId' : ?0 }")
    Optional<ConsentEntity> findByConsentId(String consentId);
}
