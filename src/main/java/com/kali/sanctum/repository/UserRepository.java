package com.kali.sanctum.repository;

import com.kali.sanctum.enums.Role;
import com.kali.sanctum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(Role role);
    User findByEmail(String email);
    boolean existsByEmail(String email);
}
