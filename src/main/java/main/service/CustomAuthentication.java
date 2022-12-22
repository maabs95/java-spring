package main.service;

import jakarta.annotation.Resource;
import main.database.QueryRepoMapper;
import main.dto.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomAuthentication implements AuthenticationProvider {

    @Resource(name="main.database.QueryRepoMapper")
    QueryRepoMapper queryRepoMapper;

    @Autowired
    PasswordEncoder passwordEncoder;
ww

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserData userData = queryRepoMapper.getUserByUsername(username);
        if(queryRepoMapper.getUserByUsername(username) == null){
            throw new UsernameNotFoundException("UserNotFound");
        }

        if(passwordEncoder.matches(password, userData.getPassword())){
            return new UsernamePasswordAuthenticationToken(username,password,new ArrayList<>());
        } else {
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
