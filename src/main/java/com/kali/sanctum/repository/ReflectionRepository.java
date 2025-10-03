package com.kali.sanctum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kali.sanctum.model.Reflection;

public interface ReflectionRepository extends JpaRepository<Reflection, Long> {

}