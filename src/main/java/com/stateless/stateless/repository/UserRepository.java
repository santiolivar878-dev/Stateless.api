package com.stateless.stateless.repository;

import com.stateless.stateless.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // Equivalente a la lógica when($request->search...) de Laravel
    @Query("SELECT u FROM User u JOIN u.role r WHERE " +
           "(:search IS NULL OR u.name LIKE %:search% OR u.email LIKE %:search%) AND " +
           "(:roleId IS NULL OR r.id = :roleId)")
    List<User> searchUsers(@Param("search") String search, @Param("roleId") Long roleId);

    long countByRoleName(String roleName);
}