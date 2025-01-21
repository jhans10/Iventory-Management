package com.example.security.security;


import com.example.security.dto.TokenDTO;
import com.example.security.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenGenerator {


    @Autowired
    private JwtEncoder accessTokenEncoder;


    @Qualifier("jwtRefreshTokenEncoder")
    @SuppressWarnings("SpringQualifierCopyableLombok")
    @Autowired
    private JwtEncoder refreshTokenEncoder;




    private String createAccessToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        Instant now = Instant.now();

     //   String roles1 = user.getRoles().stream().map(role->"ROLE"+role).collect(Collectors.joining(","));
        String roles = authentication.getAuthorities().stream().map(role->"ROLE_"+role.getAuthority()).collect(Collectors.joining(","));

        System.out.println(roles);
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("myApp")
                .issuedAt(now)
                .expiresAt(now.plus(5, ChronoUnit.MINUTES))
                .subject(String.valueOf(user.getId()))
                .claim("role",roles)
                .build();

        System.out.println(claimsSet.getClaims());


        return accessTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();


    }

    private String createRefreshToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        Instant now = Instant.now();

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder().issuer("myApp")
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.DAYS))
                .subject(String.valueOf(user.getId())).build();

        return refreshTokenEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }


    public TokenDTO crateToken(Authentication authentication){

        if (!(authentication.getPrincipal() instanceof User user)){
            throw new BadCredentialsException(MessageFormat.format("principal {0} is not ,of user type ", authentication.getPrincipal().getClass()));
        }

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setUserId(user.getId());
        tokenDTO.setAccessToken(createAccessToken(authentication));

        String refreshToken;

        if (authentication.getCredentials() instanceof Jwt jwt){
            Instant now = Instant.now();

            Instant expiresAt = jwt.getExpiresAt();
            Duration duration = Duration.between(now, expiresAt);
            long dayUntilExpired = duration.toDays();
            if (dayUntilExpired<7){
                refreshToken = createRefreshToken(authentication);
            }else {
                refreshToken = jwt.getTokenValue();
            }
        }else {
            refreshToken = createRefreshToken(authentication);
        }

        tokenDTO.setRefreshToken(refreshToken);




        return tokenDTO;


    }



}
