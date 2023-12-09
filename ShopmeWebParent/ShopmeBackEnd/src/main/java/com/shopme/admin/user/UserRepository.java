package com.shopme.admin.user;

import com.shopme.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.email = :email")
    User getUserByEmail(@Param("email") String email);

    Long countById(Integer id);
    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
    @Modifying
    void updateEnabled(Integer id, boolean enabled);

    @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.firstName, ' ', u.lastName, ' ', u.email) LIKE %?1%")
    Page<User> findAll(String keyword, Pageable pageable);

}
