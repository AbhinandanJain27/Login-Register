package com.Abhinandan.Ecommerce.Service;

import com.Abhinandan.Ecommerce.Entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    Optional<User> findByEmail(String email);
    List<User> getAllUsers();
    boolean deleteUser(String email);
    Optional<User> updateAccountStatus(String email, User userDetails);
}
