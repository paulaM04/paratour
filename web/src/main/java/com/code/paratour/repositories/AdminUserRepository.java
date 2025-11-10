package com.code.paratour.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code.paratour.model.AdminUser;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
}
