package main.service;

import jakarta.annotation.Resource;
import main.database.QueryRepoMapper;
import main.dto.UserPrincipal;
import main.dto.UserData;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class UserService implements UserDetailsService {

    @Resource(name="main.database.QueryRepoMapper")
    QueryRepoMapper queryRepoMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserData userData = queryRepoMapper.getUserByUsername(username);

        if(userData == null){
            throw new UsernameNotFoundException(format("User: %s, not found", username));
        }

        return new UserPrincipal(userData);
    }
}
