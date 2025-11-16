package com.microservice.microservice_user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserPreferencesDTO {

    private UUID id;
    private UUID userId;
    private String theme;
    private String language;
    private Boolean notificationsEnabled;
    private Boolean emailNotifications;
    private Boolean pushNotifications;
}
