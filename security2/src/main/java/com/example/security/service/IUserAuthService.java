package com.example.security.service;

import com.example.security.dto.LoginDTO;
import com.example.security.dto.TokenDTO;
import com.example.security.model.User;
import org.springframework.http.ResponseEntity;

public interface IUserAuthService {

    public ResponseEntity<?> register(User user);

    public  ResponseEntity<?> login(LoginDTO loginDTO);

    public  ResponseEntity<?> token(TokenDTO tokenDTO);

    public ResponseEntity<?> updateUser(Long id, User user);
}
