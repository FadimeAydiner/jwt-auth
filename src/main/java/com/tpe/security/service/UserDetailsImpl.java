package com.tpe.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tpe.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String userName;
    @JsonIgnore//if this class is somehow returned to front end, password will be not be visible
    private String password;
    //any class which extends from GrantedAuthority
    private Collection<? extends GrantedAuthority> authorities;

    //method which will convert User Pojo class to UserDetail class
    public static UserDetailsImpl build(User user){
       List<SimpleGrantedAuthority> authorityList= user.getRoles().stream().
                map(role -> new SimpleGrantedAuthority(role.getName().name())).
                collect(Collectors.toList());

       return new UserDetailsImpl(user.getId(),user.getUserName(),user.getPassword(),authorityList);
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
