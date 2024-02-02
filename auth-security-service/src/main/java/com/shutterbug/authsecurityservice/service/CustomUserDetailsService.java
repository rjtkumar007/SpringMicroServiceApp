package com.shutterbug.authsecurityservice.service;

import com.shutterbug.authsecurityservice.entity.UserCredential;
import com.shutterbug.authsecurityservice.repository.AuthSecurityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    AuthSecurityRepository authSecurityRepository;

    @Override
    public UserDetails loadUserByUsername ( String username ) throws UsernameNotFoundException {
        UserCredential userCredential = authSecurityRepository.findByUserName(username);
        UserDetails userDetails = new CustomUserDetails(userCredential.getUserName(), userCredential.getPassword());
        return userDetails;
    }
}
