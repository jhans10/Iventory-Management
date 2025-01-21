package com.example.security.security;

import com.example.security.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class JwtToUserConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {
    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt source) {
        User user = new User();
       // return new UsernamePasswordAuthenticationToken(user, source, Collections.emptyList());
        return new UsernamePasswordAuthenticationToken(user, source, user.getAuthorities());

    }

    @Override
    public <U> Converter<Jwt, U> andThen(Converter<? super UsernamePasswordAuthenticationToken, ? extends U> after) {
        return Converter.super.andThen(after);
    }
}
