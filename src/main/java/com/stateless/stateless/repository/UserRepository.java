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
    
    @Query("SELECT u FROM User u WHERE (:search IS NULL OR u.name LIKE %:search% OR u.email LIKE %:search%) AND (:roleId IS NULL OR u.role.id = :roleId)")
    List<User> searchUsers(@Param("search") String search, @Param("roleId") Long roleId);

    // Método que cuenta usuarios por el nombre del rol (admin, cliente, empleado)
    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);
}