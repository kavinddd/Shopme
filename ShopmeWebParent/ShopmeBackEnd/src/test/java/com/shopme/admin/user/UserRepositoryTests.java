package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.Assert;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {
    @Autowired
    private UserRepository repo;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateUserWithOneRole() {
        Role roleAdmin = entityManager.find(Role.class, 1);
        User userPrommest = new User("prommest123@hotmail.com","Xkavinddd1", "Prommest", "Kavindechatorn");
        userPrommest.addRole(roleAdmin);

        User savedUser = repo.save(userPrommest);
        assertTrue(savedUser.getId() > 0);

    }

    @Test
    public void testCreateUserWithTwoRoles() {
        entityManager.find(Role.class, 1);
        User userPrommin = new User("wanlnw879@hotmail.com", "wanlnw879", "Promminna", "Kavindechatorn");
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);

        userPrommin.addRole(roleEditor);
        userPrommin.addRole(roleAssistant);

        User savedUser = repo.save(userPrommin);
        assertTrue(savedUser.getRoles().size() > 10);
    }

    @Test
    public void testListAllUsers() {
        Iterable<User> users = repo.findAll();
        users.forEach(System.out::println);
    }

    @Test
    public void testGetUserById() {
        User user = repo.findById(2).orElse(null);
        System.out.println(user);
        assert user != null;
    }

    @Test
    public void testUpdateUserDetails() {
        User user = repo.findById(2).get();
        user.setEnabled(false);
        user.setEmail("testUpdateEmail@gmail.com");
        repo.save(user);
//        entityManager.merge(user);
        System.out.println(repo.findById(2));
        assert !user.isEnabled();
    }

    @Test
    public void testUpdateUserRoles() {
        User user = repo.findById(4).get();
        System.out.println(user.getRoles());
        Role roleEditor = new Role(3);
        Role roleSalesperson = new Role(2);
        user.getRoles().remove(roleEditor);
        user.addRole(roleSalesperson);
        System.out.println(user.getRoles());

        repo.save(user);
    }
    @Test
    public void testDeleteUser() {
        Integer userId = 2;
        repo.deleteById(2);

        User deletedUser = repo.findById(2).orElse(null);
        assert deletedUser == null;
    }

    @Test
    public void testGetUserByEmail() {
        String email = "ravi@gmail.com";
        User user =  repo.getUserByEmail(email);
        Assert.notNull(user, "It's a null");
    }

    @Test
    public void testCountById() {
        Integer id = 3;
        Long count = repo.countById(id);
        Assert.notNull(count, "It's a null");
        assert count > 0;
    }

    @Test
    public void testDisableUser() {
        Integer id=3;
        repo.updateEnabled(id, false);
    }
    @Test
    public void testEnableUser() {
        Integer id=3;
        repo.updateEnabled(id, true);
    }

    @Test
    public void testListFirstPage() {
        int pageNumber = 1;
        int pageSize = 4; Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repo.findAll(pageable);
        List<User> listUsers = page.getContent();
        listUsers.forEach(user -> System.out.println(user));
        assert listUsers.size() == pageSize;
    }

    @Test
    public void testSearchUsers() {
        String keyword = "bruce";
        int pageNumber = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repo.findAll(keyword, pageable);

        List<User> listUsers = page.getContent();
        listUsers.forEach(user -> System.out.println(user));

        assert (listUsers.size() > 0);
    }
}
