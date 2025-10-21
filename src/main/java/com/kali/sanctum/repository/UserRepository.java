package com.kali.sanctum.repository;

import com.kali.sanctum.enums.Role;
import com.kali.sanctum.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByRole(Role role, Pageable pageable);
    User findByEmail(String email);
    boolean existsByEmail(String email);
}
