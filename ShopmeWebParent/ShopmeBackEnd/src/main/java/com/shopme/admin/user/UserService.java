package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
   List<User> listAll();

   Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword);
   List<Role> listRoles();

   User save(User user);

   User updateAccount(User newUserInformation);
   boolean isEmailUnique(String email, Integer id);

   User get(Integer id) throws UserNotFoundException;
   User getByEmail(String email);

   void delete(Integer id) throws UserNotFoundException;

   void updateUserEnabledStatus(Integer id, boolean enabled);


}
