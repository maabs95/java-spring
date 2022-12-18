package main.controller;

import jakarta.annotation.Resource;
import main.config.SecurityConfig;
import main.database.QueryRepoMapper;
import main.service.CustomAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import main.dto.UserData;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/v1")
public class UserController {

    @Resource(name="main.database.QueryRepoMapper")
    QueryRepoMapper queryRepoMapper;

    private Map<String, UserData> returnValue = new HashMap<>();

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomAuthentication customAuthentication;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResponseEntity<Object> login(@RequestBody UserData userData) throws Exception{

        Authentication authObject;
        try{
            authObject = customAuthentication.authenticate(new UsernamePasswordAuthenticationToken(userData.getUsername(),userData.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authObject);

            System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

//            var claims =
//                    JwtClaimsSet.builder()
//                            .issuer("example.io")
//                            .subject(format("%s,%s", user.getUsername()))
//                            .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "sdfsgsdghergwrger")
                    .body("{message: logged in}");

        } catch (BadCredentialsException e){
            return new ResponseEntity<>("{message:Wrong Credential}", HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<Object> getUsers(){
        List<UserData> getResult = queryRepoMapper.getUserList();
        return new ResponseEntity<>(getResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/getUserByUsername", method = RequestMethod.GET)
    public ResponseEntity<Object> getUserByUsername(@RequestParam String username){
        return new ResponseEntity<>(queryRepoMapper.getUserByUsername(username), HttpStatus.OK);
    }

    @RequestMapping(value="/loggedInUser", method = RequestMethod.GET)
    public ResponseEntity<Object> loggedInUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<>(auth.getPrincipal(), HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(@RequestBody UserData userData){

        UserData u = queryRepoMapper.getUserByUsername(userData.getUsername());
        if(u != null && notEmpty(u.getUsername())){
            return new ResponseEntity<>("{\"message\": \"Duplication is not allowed\"}", HttpStatus.FORBIDDEN);
        }

        HashMap<String, String> validate = validateField(userData);
        if(validate != null){
            return new ResponseEntity<>(validate, HttpStatus.FORBIDDEN);
        }

        String password = userData.getPassword();
        password = passwordEncoder.encode(password);
        userData.setPassword(password);

        if(queryRepoMapper.insertUser(userData) != 1){
            return new ResponseEntity<>("{\"message\": \"Error\"}", HttpStatus.BAD_REQUEST);
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserData) {
            String username = ((UserData)principal).getUsername();
            System.out.println("username >> " + username);
        } else {
            String username = principal.toString();
        }

        return new ResponseEntity<>(userData, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/editUser", method = RequestMethod.POST)
    public ResponseEntity<Object> editUser(@RequestBody UserData userData){
        HashMap<String,String> message = validateField(userData);
        if(message != null){
            return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
        }

        String password = userData.getPassword();
        password = passwordEncoder.encode(password);
        userData.setPassword(password);

        if(queryRepoMapper.updateUser(userData) != 1){
            return new ResponseEntity<>("{\"message\": \"Error\"}", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userData, HttpStatus.OK);

    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteUsers(@RequestParam String username){

        if(queryRepoMapper.deleteUser(username) != 1){
            return new ResponseEntity<>("{\"message\": \"Error\"}", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("{message: " + username + " deleted}", HttpStatus.OK);
    }

    private HashMap<String, String> validateField(UserData d){

        HashMap<String,String> erroMessage = new HashMap<>();

        if(!notEmpty(d.getUsername())){
            erroMessage.put("username","Fill in username");
        }

        if(!notEmpty(d.getFirstname())){
            erroMessage.put("firstname","Fill in first name");
        }

        if(!notEmpty(d.getLastname())){
            erroMessage.put("lastname","Fill in lastname");
        }


        return null;
    }

    private boolean notEmpty(String value){
        return value != null && !value.isEmpty();
    }

}
