package com.dsena7.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "consent_entity")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsentEntity {

    @Id
    private String id;

    @Field("consentId")
    @Indexed
    private String consentId;

    @Field("state")
    private ConsentStateEnum state;

    @Field("updateStatus")
    private LocalDateTime updateStatus;

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("expiratedAt")
    private LocalDateTime expiratedAt;
}