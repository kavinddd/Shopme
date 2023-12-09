package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {
    final public static int USERS_PER_PAGE = 5;
    final private UserRepository userRepository;
    final private RoleRepository roleRepository;
    final private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> listAll() {
        List<User> users = userRepository.findAll();
        return users;
    }

    @Override
    public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {

        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

        if (keyword != null) {
            return userRepository.findAll(keyword, pageable);
        }
        return userRepository.findAll(pageable);
    }

    @Override
    public List<Role> listRoles() {
        return  (List<Role>) roleRepository.findAll();
    }

    @Override
    public User save(User user) {
        boolean isUpdatingUser = (user.getId() != null);
        boolean isPasswordEmpty = (user.getPassword().isEmpty());

        String newPassword;
        // no need of encoding
        if (isPasswordEmpty && isUpdatingUser) {
            newPassword = userRepository.findById(user.getId()).get().getPassword();
        }
        // need of encoding (new password)
        else {
            newPassword = passwordEncoder.encode(user.getPassword());
        }

        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    @Override
    public User updateAccount(User newUserInformation) {
        // retrieve the user, we will use as a base
        User user = userRepository.findById(newUserInformation.getId()).get();

        boolean isChangingPassword = !newUserInformation.getPassword().isEmpty();
        boolean isChangingPhoto = newUserInformation.getPhotos() != null;

        if (isChangingPassword) {
            String encodedNewPassword = passwordEncoder.encode(newUserInformation.getPassword());
            user.setPassword(encodedNewPassword);
        }

        if (isChangingPhoto) {
            user.setPhotos(newUserInformation.getPhotos());
        }

        user.setFirstName(newUserInformation.getFirstName());
        user.setLastName(newUserInformation.getLastName());

        return userRepository.save(user);
    }


    @Override
    public boolean isEmailUnique(String email, Integer id) {
        User userByEmail = userRepository.getUserByEmail(email);

        // This method is used by two situation : 1. Creating new user (id = null) 2. Editing user data (id = not null)
        // 1. No user found = the email is unique
        if (userByEmail == null) return true;

        // 2. If editing, user found id == edited id -> the email is unique
        if (id != null) return userByEmail.getId() == id;

        return false;
    }

    @Override
    public User get(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public void delete(Integer id) throws UserNotFoundException {
        Long countById = userRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("The user is not found with ID " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateUserEnabledStatus(Integer id, boolean enabled) {
        userRepository.updateEnabled(id, enabled);
    }


}
