package com.dsena7.repository;

import com.dsena7.model.ConsentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsentRepository extends MongoRepository<ConsentEntity, String> {
    Optional<ConsentEntity> findByConsentId(String consentId);
}