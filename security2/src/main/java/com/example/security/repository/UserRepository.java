package com.example.security.repository;

import com.example.security.dto.RoleDTO;
import com.example.security.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {



    public boolean existsByUserName(String username);

    public Optional<User>  findByUserName(String userName);

    // Consulta ajustada para retornar una lista de RoleDTO
    @Query("SELECT new com.example.security.dto.RoleDTO(r.id, r.rolName) FROM User u JOIN u.roles r WHERE u.userName = :userName")
    List<RoleDTO> findRolesByUserName(@Param("userName") String userName);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM users_roles WHERE user_id = :userId AND role_id NOT IN :rolesId", nativeQuery = true)
    void deleteUserRoleRelation(@Param("userId") Long userId, @Param("rolesId") List<Long> rolesId);


    /*
    *
    * @Modifying
@Query("INSERT INTO UserRole (user, role) SELECT :user, r FROM Role r WHERE r.id IN :roleIds AND r.id NOT IN (SELECT ur.role.id FROM UserRole ur WHERE ur.user.id = :userId)")
void addRolesNotIn(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds, @Param("user") User user);

    *
    * */

    @Transactional
    @Modifying
    @Query(
            value = """
    INSERT INTO users_roles (user_id, role_id)
    SELECT :userId, r.role_id
    FROM role r
    WHERE r.role_id IN :roleIds
      AND r.role_id NOT IN (
          SELECT ur.role_id
          FROM users_roles ur
          WHERE ur.user_id = :userId
      )
    """,
            nativeQuery = true
    )
    void addRolesNotIn(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);






}
