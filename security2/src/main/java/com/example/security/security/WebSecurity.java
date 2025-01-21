package com.example.security.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Slf4j
public class WebSecurity {

    @Autowired
    private JwtToUserConverter jwtToUserConverter;



    @Autowired
    private KeyUtils keyUtils;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserDetailsManager userDetailsManager;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeHttpRequests((authorized)->{
            authorized.requestMatchers("/auth/**").permitAll().requestMatchers("/api-product-controller/**").hasRole("ADMIN").anyRequest().authenticated();
        }).csrf((csrf)->csrf.disable()).cors((cors)->cors.disable()).httpBasic((httpBasic)->httpBasic.disable())
                .oauth2ResourceServer((oauth2)->{
                    oauth2.jwt((jwt)->{
                        jwt.jwtAuthenticationConverter(jwtToUserConverter);
                    });
                }).sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling((exceptions)-> exceptions
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler()));


        return httpSecurity.build();

    }



    @Bean
    @Primary
    public JwtDecoder jwtAccessTokenDecoder(){

        return NimbusJwtDecoder.withPublicKey(keyUtils.getAccessTokenPublicKey()).build();
    }

    @Bean
    @Primary
    public JwtEncoder jwtAccessTokenEncoder(){

        JWK jwk = new RSAKey.Builder(keyUtils.getAccessTokenPublicKey()).privateKey(keyUtils.getAccessTokenPrivateKey())
                .build();

        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);


    }

    @Qualifier("jwtRefreshTokenDecoder")
    @Bean
    public JwtDecoder jwtRefreshTokenDecoder(){
        return NimbusJwtDecoder.withPublicKey(keyUtils.getRefreshTokenPublicKey()).build();
    }

    @Qualifier("jwtRefreshTokenEncoder")
    @Bean
   public JwtEncoder jwtRefreshTokenEncoder(){

        JWK jwk = new RSAKey.Builder(keyUtils.getRefreshTokenPublicKey()).privateKey(keyUtils.getRefreshTokenPrivateKey())
                .build();

        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));

        return new NimbusJwtEncoder(jwks);

    }



    @Qualifier("jwtRefreshTokenAuthProvider")
    @Bean
    public JwtAuthenticationProvider jwtRefreshTokenAuthProvider(){
        JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtRefreshTokenDecoder());
        jwtAuthenticationProvider.setJwtAuthenticationConverter(jwtToUserConverter);
        return jwtAuthenticationProvider;
    }





    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsManager);
        return daoAuthenticationProvider;
    }







}
