package com.code.paratour.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code.paratour.model.User;

public interface UserRepository extends JpaRepository<User, String> {
}
