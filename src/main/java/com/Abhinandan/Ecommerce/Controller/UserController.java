package com.Abhinandan.Ecommerce.Controller;


import com.Abhinandan.Ecommerce.Entity.User;
import com.Abhinandan.Ecommerce.Enums.AccountStatus;
import com.Abhinandan.Ecommerce.Enums.UserRole;
import com.Abhinandan.Ecommerce.Service.UserService;
import com.Abhinandan.Ecommerce.Utility.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "https://localhost:4200")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtility jwtUtility;

    // Api For User Registration
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user){
        user.setUserRole(UserRole.CUSTOMER);
        user.setAccountStatus(AccountStatus.ACTIVE);
        String password = user.getPassword();
        try{
            user.setPassword(toHexString(getSHA(password)));
        }
        catch (NoSuchAlgorithmException e){
            System.out.println("Exception!!! Incorrect Algorithm!!!");
        }
        return new ResponseEntity<User>(userService.saveUser(user), HttpStatus.CREATED);
    }

//    @PostMapping("/auth")
//    public ResponseEntity<>
    // Api For User Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        Map<String, String> response = new HashMap<>();

        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isPresent()) {
            AccountStatus status = optionalUser.get().getAccountStatus();
            if(status == AccountStatus.BLOCKED){
                response.put("message","Your Account Has been blocked");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            User user = optionalUser.get();
            String hashedPassword = "";
            String jwtToken = jwtUtility.generateToken(new HashMap<>(),user);
            try {
                hashedPassword = toHexString(getSHA(password));
            } catch (Exception e) {
                response.put("message", "Error processing request.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            if (hashedPassword.equals(user.getPassword())) {
                response.put("Authorization",jwtToken);
                response.put("message", "Login successful!");
                response.put("role", user.getUserRole().name());
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Invalid password.");
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.put("message", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/getAllUsers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Delete a user or account closure
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        boolean deleted = userService.deleteUser(email);
        if (deleted) {
            return ResponseEntity.noContent().build(); // Return 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found
        }
    }

    // Updating account status
    @PutMapping("/modifyAccountStatus/{email}")
    public ResponseEntity<User> updateAccountStatus(@PathVariable String email, @RequestBody User userDetails) {
        Optional<User> updatedUser = userService.updateAccountStatus(email, userDetails);
        return updatedUser.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);
        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));
        // Pad with leading zeros
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }
}
