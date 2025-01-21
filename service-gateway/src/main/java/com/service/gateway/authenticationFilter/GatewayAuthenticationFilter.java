package com.service.gateway.authenticationFilter;

import com.service.gateway.managementToken.JwtUtilsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;



@Component
@RefreshScope
public class GatewayAuthenticationFilter extends AbstractGatewayFilterFactory<GatewayAuthenticationFilter.Config> {

    @Autowired
    private RouterValidator validator;

    @Autowired
    private JwtUtilsService jwtUtil;


    @Autowired
    private JwtUtilsService jwtUtilsService;

    public GatewayAuthenticationFilter(){
        super(Config.class);
    }



    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                System.out.println("request succefull");

                try {
                    HttpHeaders headers = exchange.getRequest().getHeaders();

                    if (!headers.containsKey(HttpHeaders.AUTHORIZATION)){
                        throw  new RuntimeException("Missing Authorization Headers");
                    }

                    String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);



                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        authHeader = authHeader.substring(7);
                    }else {
                        throw  new RuntimeException("Invalid Authorization Header Format");
                    }


//                    //REST call to AUTH service
//                    template.getForObject("http://IDENTITY-SERVICE//validate?token" + authHeader, String.class);
                    jwtUtil.validateToken(authHeader);
                    Claims claims= Jwts.parserBuilder().setSigningKey(jwtUtilsService.getSignKey()).build().parseClaimsJws(authHeader).getBody();





                } catch (RuntimeException e) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();

                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }



}
