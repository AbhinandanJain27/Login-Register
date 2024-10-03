package com.Abhinandan.Ecommerce.Entity;

import com.Abhinandan.Ecommerce.Enums.AccountStatus;
import com.Abhinandan.Ecommerce.Enums.SecurityQuestion;
import com.Abhinandan.Ecommerce.Enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "Users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;

    private String name;

    private long mobileNumber;

    private String email;

    private String password;

    private SecurityQuestion securityQuestion;

    private String securityQuestionAnswer;

    private UserRole userRole;

    private AccountStatus accountStatus;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getPassword(){
        return password;
    }
}
