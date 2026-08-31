package com.stateless.stateless.repository;

import com.stateless.stateless.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailToken(String emailToken);
    
    @Query("SELECT COUNT(u) FROM User u JOIN u.role r WHERE r.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u WHERE (:search IS NULL OR u.name LIKE %:search% OR u.email LIKE %:search%)")
    List<User> searchUsers(@Param("search") String search);
}