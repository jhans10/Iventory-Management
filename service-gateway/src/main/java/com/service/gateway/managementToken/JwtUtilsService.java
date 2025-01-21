package com.service.gateway.managementToken;


import io.jsonwebtoken.Jwts;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.PublicKey;


@Component
public class JwtUtilsService {


    @Autowired
    private AccessToken accessToken;


    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";


    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }



    public Key getSignKey() {
        PublicKey publicKey = accessToken.getAccessTokenPublicKey();
        return publicKey;
    }


}
