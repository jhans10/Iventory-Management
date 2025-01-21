package com.example.security.service;

import com.example.security.dto.LoginDTO;
import com.example.security.dto.TokenDTO;
import com.example.security.model.Role;
import com.example.security.model.User;
import com.example.security.repository.RoleRepository;
import com.example.security.repository.UserRepository;
import com.example.security.security.TokenGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserAuthService implements IUserAuthService{


    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private DaoAuthenticationProvider daoAuthenticationProvider;

    @Qualifier("jwtRefreshTokenAuthProvider")
    @Autowired
    private JwtAuthenticationProvider refreshTokenAuthProvider;

    @Autowired
    private UserRepository userRepository;





    @Override
    public ResponseEntity<?> register(User user) {
        try {
            if (user ==null || user.getPassword() == null || user.getRoles() == null){
                return  ResponseEntity.badRequest().body("Invalid user objects or missing fields");
            }else {
                User userReceive = user;


                user.setPassword(passwordEncoder.encode(userReceive.getPassword()));
                user.getRoles().stream().forEach(f->roleRepository.save(f));
                userDetailsManager.createUser(user);
                // User user1 = userRepository.save(user);
                List<SimpleGrantedAuthority> authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRolName())).collect(Collectors.toList());

                Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(user, user.getPassword(), authorities);
                return ResponseEntity.ok(tokenGenerator.crateToken(authentication));
            }
        }catch (DataIntegrityViolationException e){
            log.warn("Failed login attempt for user : ",e);
            return  ResponseEntity.status(HttpStatus.CONFLICT).body("user already exists or violates constraints");

        }catch (IllegalArgumentException e){
            log.error("Invalid arguments during login : ",e);
            return  ResponseEntity.badRequest().body("Invalid arguments provided :"+e.getMessage());
        }catch (Exception e){
            log.error("Unexpected error during login : ",e);
            return  ResponseEntity.internalServerError().body("An Unexpected error occurred :"+e.getMessage());
        }

    }

    @Override
    public ResponseEntity<?> login(LoginDTO loginDTO) {

        try {
            if (loginDTO == null || loginDTO.getPassword() == null || loginDTO.getUserName() == null ){
                return ResponseEntity.badRequest().body(" invalid user objects or missing fields");
            }
                Authentication authentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getUserName(), loginDTO.getPassword()));
                return ResponseEntity.ok(tokenGenerator.crateToken(authentication));

        }catch (AuthenticationException e){
            log.warn("Failed login attempt for user :{}", loginDTO.getUserName());
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }catch (IllegalArgumentException e){
            log.error("Invalid arguments during login :",e);
            return  ResponseEntity.badRequest().body("Invalid arguments : "+e.getMessage());
        }catch (Exception e){
            log.error("Unexpected error during login :",e);
            return  ResponseEntity.internalServerError().body("Unexpected Error occurred : "+e.getMessage());
        }



    }

    @Override
    public ResponseEntity<?> token(TokenDTO tokenDTO) {
        try {
            if (tokenDTO.getUserId() == null || tokenDTO.getRefreshToken() ==null || tokenDTO.getAccessToken() ==null){
                return ResponseEntity.badRequest().body("Invalid user objects or missing fields");
            }
            Authentication authentication = refreshTokenAuthProvider.authenticate(new BearerTokenAuthenticationToken(tokenDTO.getRefreshToken()));

            Jwt jwt = (Jwt) authentication.getPrincipal();
            return ResponseEntity.ok(Map.of("accesToken", jwt.getTokenValue(), "refreshToken", tokenDTO.getRefreshToken(), "userDetails", jwt.getClaims().get("userDetails")));
        }catch (AuthenticationException e){
            log.warn("Failed login attempt for user :{}", tokenDTO.getUserId());
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }catch (IllegalArgumentException e){
            log.error("Invalid arguments during login :",e);
            return  ResponseEntity.badRequest().body("Invalid arguments : "+e.getMessage());
        }catch (Exception e){
            log.error("Unexpected error during login :",e);
            return  ResponseEntity.internalServerError().body("Unexpected Error occurred : "+e.getMessage());
        }



    }

    @Override
    public ResponseEntity<?> updateUser(Long id, User user) {
        try {
            if (user == null || id == null || user.getUsername() == null || user.getPassword() == null) {
                return ResponseEntity.badRequest().body("Invalid user objects or missing fields");
            }
            updateUserWithRoles(user);
            return ResponseEntity.ok().body(user);

        } catch (IllegalArgumentException e) {
            log.error("Invalid arguments during login :", e);
            return ResponseEntity.badRequest().body("Invalid arguments : " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during login :", e);
            return ResponseEntity.internalServerError().body("Unexpected Error occurred : " + e.getMessage());
        }

    }


    @Transactional
    public void updateUserWithRoles(User user) {
        try {
            if (user!=null) {
                User userToUpdate = userRepository.findByUserName(user.getUsername()).orElseThrow(() -> {
                    throw new UsernameNotFoundException("user don't exist");
                });
                userToUpdate.setUserName(user.getUsername());
                userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));


                // ((User) user).setPassword(passwordEncoder.encode(user.getPassword()));
                // userRepository.save((User) user);

                updateRoles(userToUpdate.getId(), user.getRoles());
                userRepository.save(userToUpdate);
            }
        }catch (IllegalArgumentException e){
            log.error("user can't be null");
            throw  new RuntimeException(" object can't be null ");

        }



    }



    @Transactional
    public  void updateRoles(Long userId,List<Role> roles){
        try {
            if (!roles.isEmpty()  && userId!=null ) {

                List<Long> idNewRoles =roles.stream().map(r->r.getId()).collect(Collectors.toList());
                if (!idNewRoles.isEmpty()) {
                    userRepository.deleteUserRoleRelation(userId, idNewRoles);
                    userRepository.addRolesNotIn(userId, idNewRoles);

                }
            }
        }catch (IllegalArgumentException e){
            log.error("Objects can't be null or fields missing ");
            throw new   IllegalArgumentException(" Objects can't be null or fields missing ");
        }

    }

}
