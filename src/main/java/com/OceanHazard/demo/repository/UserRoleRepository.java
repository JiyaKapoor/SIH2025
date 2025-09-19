package com.OceanHazard.demo.repository;

import com.OceanHazard.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<User,Long> {
}
