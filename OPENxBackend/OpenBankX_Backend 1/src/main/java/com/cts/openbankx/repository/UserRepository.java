package com.cts.openbankx.repository;

import com.cts.openbankx.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);


    List<User> findByNameIgnoreCase(String name);


    List<User> findByEmailContainingIgnoreCase(String fragment);
}
