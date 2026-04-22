package com.macdduck.dram.user.repository;

import com.macdduck.dram.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}