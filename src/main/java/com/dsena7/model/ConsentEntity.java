package com.dsena7.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "consentEntity")
@Data
@Builder
public class ConsentEntity {

    private String id;

    @Field("consentId")
    private String consentId;

    @Field("state")
    private ConsentStateEnum state;

    @Field("updateStatus")
    private LocalDateTime updateStatus;

}
