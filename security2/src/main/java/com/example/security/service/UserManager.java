package com.example.security.service;

import com.example.security.model.Role;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserManager implements UserDetailsManager {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void createUser(UserDetails user) {

        System.out.println(user.getAuthorities());

        ((User)user).setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save((User)user);

    }

    @Override
    public void updateUser(UserDetails user) {


    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }







    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUserName(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserName(username).orElseThrow(()->new UsernameNotFoundException(MessageFormat.format(" User with username  {0} not found", username)));
    }
}
