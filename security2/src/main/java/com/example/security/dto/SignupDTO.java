package com.example.security.dto;

import lombok.*;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupDTO {

    private String userName;

    private String password;
}
