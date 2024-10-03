package com.Abhinandan.Ecommerce.Service.Implementation;

import com.Abhinandan.Ecommerce.DTO.loginDTO;
import com.Abhinandan.Ecommerce.Entity.User;
import com.Abhinandan.Ecommerce.Enums.AccountStatus;
import com.Abhinandan.Ecommerce.Repository.UserRepository;
import com.Abhinandan.Ecommerce.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public User saveUser(User user){
        return userRepository.save(user);
    }
    public Optional<User> loginUser(loginDTO loginDto){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword()));
        return userRepository.findByEmail(loginDto.getEmail());
    }
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Override
    public boolean deleteUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return true;
        }
        return false;
    }
    @Override
    public Optional<User> updateAccountStatus(String email, User userDetails) {
        return userRepository.findByEmail(email).map(user -> {
            if(user.getAccountStatus().equals(AccountStatus.ACTIVE)){
                user.setAccountStatus(AccountStatus.BLOCKED);
            }else {
                user.setAccountStatus(AccountStatus.ACTIVE);
            }
            return userRepository.save(user); // Save updated use
        });
    }

}
