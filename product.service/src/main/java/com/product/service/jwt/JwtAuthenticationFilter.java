package com.product.service.jwt;

import com.product.service.accessToken.AccessToken;
import com.product.service.accessToken.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.util.StringUtil;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;



import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    @Autowired
    private AccessToken accessToken;

    @Autowired
    private  JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = request.getHeader("Authorization");
            if (StringUtils.hasText(token)  && token.startsWith("Bearer ") ){

                token = token.substring(7).trim();


                Claims claims = jwtUtils.getClaims(token);

                String roles = (String) claims.get("role");
                if (roles != null) {
                    // Si roles no es null, separamos los roles en un array
                    String[] rolesArray = roles.split(",");

                    // Crear los authorities con el prefijo ROLE_
                    List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesArray)
                            .map(role -> new SimpleGrantedAuthority(role))
                            .collect(Collectors.toList());

                    // Crear el token de autenticación
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            claims.getSubject(), null, authorities);  // Asignamos los authorities

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    // Si no se encuentra el claim "roles", puede lanzar un error o asignar un rol por defecto
                    throw new BadCredentialsException("El token no contiene roles.");
                }

            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        filterChain.doFilter(request, response);

    }
}
