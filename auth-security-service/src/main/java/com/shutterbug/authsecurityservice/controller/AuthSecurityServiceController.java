package com.shutterbug.authsecurityservice.controller;

import com.shutterbug.authsecurityservice.dto.request.UserAuthRequestDto;
import com.shutterbug.authsecurityservice.dto.request.UserRequestDto;
import com.shutterbug.authsecurityservice.entity.UserCredential;
import com.shutterbug.authsecurityservice.service.AuthSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthSecurityServiceController {
    private  final AuthSecurityService authSecurityService;
    private  final AuthenticationManager authenticationManager;

    @Autowired
    public AuthSecurityServiceController ( AuthSecurityService authSecurityService, AuthenticationManager authenticationManager ) {
        this.authSecurityService = authSecurityService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public UserCredential registerUser( @RequestBody UserRequestDto userRequestDto ) {
        return authSecurityService.saveUser(userRequestDto);
    }

    @PostMapping("/token")
    public String generateToken( @RequestBody UserAuthRequestDto userAuthRequestDto) {
        Authentication authenticate = doAuthentication(userAuthRequestDto.getUserName(), userAuthRequestDto.getPassword());
        if(authenticate.isAuthenticated()) {
            return authSecurityService.generateToken(userAuthRequestDto);
        } else {
            throw new RuntimeException("invalid access");
        }
    }
    
    @GetMapping("/userName")
    public String getUserDetails( Principal principal) {
        return principal.getName();
    }

    private Authentication doAuthentication ( String userName, String password ) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName, password);
        try {
            return authenticationManager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Username or Password  !!");
        }
    }

    @GetMapping("/validateToken/")
    public Boolean validateToken( @RequestParam String token) {
        return authSecurityService.validateToken(token);
    }
}
