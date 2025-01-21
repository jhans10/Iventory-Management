package com.example.security.controller;

import com.example.security.dto.LoginDTO;

import com.example.security.dto.TokenDTO;
import com.example.security.model.User;

import com.example.security.service.IUserAuthService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/auth")
public class AuthenticationController {


    @Autowired
    public IUserAuthService iUserAuthService;





    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User request){
        return  iUserAuthService.register(request);

    }


    @PostMapping("/login")
    public  ResponseEntity<?> login (@RequestBody LoginDTO request){
       return  iUserAuthService.login(request);
    }


    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody TokenDTO request){
    return  iUserAuthService.token(request);
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User request){
        return  iUserAuthService.updateUser(id, request);
    }




}
