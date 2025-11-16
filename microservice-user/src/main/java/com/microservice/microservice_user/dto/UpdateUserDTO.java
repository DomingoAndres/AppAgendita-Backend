package com.microservice.microservice_user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UpdateUserDTO {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profileImageUrl;
}
