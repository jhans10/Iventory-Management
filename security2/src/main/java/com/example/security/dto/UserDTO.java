package com.example.security.dto;


import com.example.security.model.User;
import lombok.*;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {


    private Long id;

    private String userName;

    public static UserDTO from(User user){
        return UserDTO.builder().id(user.getId()).userName(user.getUsername()).build();
    }
}
