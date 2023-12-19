package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTests {
    @Autowired
    private RoleRepository repo;

    @Test
    public void testCreateFirstRole() {
        Role roleAdmin = new Role("Admin", "Manage everything");
        Role savedRole = repo.save(roleAdmin);
        assertTrue(savedRole.getId() > 0);
        assertEquals(savedRole.getName(), "Admin");
    }
    @Test
    public void testCreateRestRoles() {
        Role roleSalesperson = new Role("Salesperson",
                "manage product price, customers, shipping, orders and sales report");
        Role roleEditor = new Role("Editor",
                "manage categories, brands, products, articles and menus");
        Role roleShipper = new Role("Shipper",
                "view products ,view orders and update order status");
        Role roleAssistant = new Role("Assistant",
                "manage questions and reviews ");

        repo.saveAll(List.of(roleSalesperson, roleEditor, roleShipper, roleAssistant));
        int nRoles = 0;

        for (Role role: repo.findAll()) {
            nRoles++;
        }
        assertTrue(nRoles >= 4);

    }
}
