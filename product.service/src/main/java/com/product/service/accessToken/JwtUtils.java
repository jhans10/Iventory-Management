package com.product.service.accessToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    @Autowired
    private AccessToken accessToken;



    public Claims getClaims(String token){
        return Jwts.parser()
                .setSigningKey(accessToken.getAccessTokenPublicKey())
                .parseClaimsJws(token).getBody();
    }






}
