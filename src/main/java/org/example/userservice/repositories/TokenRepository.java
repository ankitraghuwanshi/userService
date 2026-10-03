package org.example.userservice.repositories;

import org.example.userservice.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {
    @Override
    Token save(Token token);

    Optional<Token> findByValueAndExpiryAtAfter(String value, Date expiryAtAfter);
}
