package com.example.security.controller;

import com.example.security.dto.UserDTO;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {


    @Autowired
    private UserRepository userRepository;

    /*
        @GetMapping("/{id}")
        @PreAuthorize("#user.id==#id")
        public ResponseEntity<?> user(@AuthenticationPrincipal User user, @PathVariable Long id){
            return ResponseEntity.ok(UserDTO.from(userRepository.findById(id).orElseThrow()));
        }
     */


    @GetMapping("/{id}")
    public ResponseEntity<?> user(@PathVariable Long id){
        return ResponseEntity.ok(UserDTO.from(userRepository.findById(id).orElseThrow()));
    }




}
