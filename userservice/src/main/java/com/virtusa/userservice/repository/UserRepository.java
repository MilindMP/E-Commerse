package com.virtusa.userservice.repository;

import com.virtusa.userservice.dto.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDTO,Long> {
    Optional<UserDTO> findByUsername(String username);
}
