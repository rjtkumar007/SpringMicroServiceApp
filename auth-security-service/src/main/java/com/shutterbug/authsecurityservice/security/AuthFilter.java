package com.shutterbug.authsecurityservice.security;

import com.shutterbug.authsecurityservice.config.JwtHelper;
import com.shutterbug.authsecurityservice.service.CustomUserDetails;
import com.shutterbug.authsecurityservice.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Autowired
    private JwtHelper jwtHelper;
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    @Override
    protected void doFilterInternal ( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain ) throws ServletException, IOException {
        var header = request.getHeader("Authorization");
        String token = header!= null ? header.substring(7) : null;
        String userName = null;
        if(token != null) {
            userName = jwtHelper.getUsernameFromToken(token);
            if(userName!= null && !userName.isBlank() && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(userName);
                boolean isValidToken = jwtHelper.validateToken(token, userDetails);
                if(isValidToken) {
                    logger.info("Valid Token and username !!");
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    logger.error("Validation fails !!");  
                }              
            } else {
                logger.error("Invalid Token !!");
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
