package com.shutterbug.authsecurityservice.service;

import com.shutterbug.authsecurityservice.config.JwtHelper;
import com.shutterbug.authsecurityservice.dto.request.UserAuthRequestDto;
import com.shutterbug.authsecurityservice.dto.request.UserRequestDto;
import com.shutterbug.authsecurityservice.entity.UserCredential;
import com.shutterbug.authsecurityservice.repository.AuthSecurityRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthSecurityService {

    private  final  AuthSecurityRepository authSecurityRepository;
    private final PasswordEncoder passwordEncoder;
    
    private final JwtHelper jwtHelper;
    
    public AuthSecurityService ( AuthSecurityRepository authSecurityRepository, PasswordEncoder passwordEncoder, JwtHelper jwtHelper ) {
        this.authSecurityRepository = authSecurityRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtHelper = jwtHelper;
    }
    
    public UserCredential saveUser( UserRequestDto userRequestDto ) {
        UserCredential userCredential = new UserCredential();
        userCredential.setEmail(userRequestDto.getEmail());
        userCredential.setUserName(userRequestDto.getUserName());
        userCredential.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        authSecurityRepository.save(userCredential);
        return userCredential;
    }

    public String generateToken ( UserAuthRequestDto userRequestDto ) {
        UserDetails userDetails = User.builder().username(userRequestDto.getUserName()).password(passwordEncoder.encode(userRequestDto.getPassword())).build();
        return jwtHelper.generateToken(userDetails);
    }

    public Boolean validateToken ( String token ) {
        return false;
    }
}

