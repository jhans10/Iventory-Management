package com.example.security.repository;

import com.example.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {


    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :id")
    public List<Role> rolesByUser(@Param("id") Long id);

}
