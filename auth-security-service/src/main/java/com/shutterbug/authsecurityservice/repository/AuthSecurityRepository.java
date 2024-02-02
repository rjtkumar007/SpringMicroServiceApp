package com.shutterbug.authsecurityservice.repository;

import com.shutterbug.authsecurityservice.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthSecurityRepository extends JpaRepository<UserCredential, Long> {
    UserCredential findByUserName ( String username );
}
