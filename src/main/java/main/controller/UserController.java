package main.controller;

import jakarta.annotation.Resource;
import main.config.SecurityConfig;
import main.database.QueryRepoMapper;
import main.dto.UserPrincipal;
import main.service.CustomAuthentication;
import main.service.UserService;
import main.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
import java.util.stream.Collectors;

import static java.lang.String.format;

//@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/v1")
public class UserController {

    @Resource(name="main.database.QueryRepoMapper")
    QueryRepoMapper queryRepoMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomAuthentication customAuthentication;

    @Autowired
    JwtUtils jwtUtils;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResponseEntity<Object> login(@RequestBody UserData userData) throws Exception{

        Authentication authObject;
        try{
            authObject = customAuthentication.authenticate(new UsernamePasswordAuthenticationToken(userData.getUsername(),userData.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authObject);

            UserData u = queryRepoMapper.getUserByUsername(userData.getUsername());
            String jwt = jwtUtils.generateJwtToken(authObject);
//            var claims =
//                    JwtClaimsSet.builder()
//                            .issuer("example.io")
//                            .subject(format("%s,%s", user.getUsername()))
//                            .build();

//            UserPrincipal userPrincipal = (UserPrincipal) authObject;

//            List<String> roles = authObject.getAuthorities().stream()
//                    .map(GrantedAuthority::getAuthority).toList();

            return ResponseEntity.ok().contentType(MediaType.valueOf("application/json"))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
//                    .header("Access-Control-Allow-Origin","http://localhost:3000")
                    .body("{\"role\":\"" + u.getRole() + "\","
                            + "\"Authorization\":\"" + jwt + "\"}"
                    );

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
        UserData u = queryRepoMapper.getUserByUsername(username);
        return new ResponseEntity<>(
                "{\"username\":\"" + u.getUsername() + "\","
                        + "\"firstname\":\"" + u.getFirstname() + "\","
                        + "\"lastname\":\"" + u.getLastname() + "\","
                        + "\"role\":\"" + u.getRole() + "\","
                        + "\"email\":\"" + u.getEmail() + "\""
                        + "}", HttpStatus.OK);
    }

    @RequestMapping(value="/loggedInUser", method = RequestMethod.GET)
    public ResponseEntity<Object> loggedInUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == "anonymousUser") return new ResponseEntity<>("{\"message\":\"false\"}", HttpStatus.UNAUTHORIZED);

        UserPrincipal u = (UserPrincipal) auth.getPrincipal();
        UserData userData = u.getUserData();

        return ResponseEntity.ok().contentType(MediaType.valueOf("application/json"))
//                .header("Access-Control-Allow-Origin","http://localhost:3000")
                .body("{\"role\":\"" + userData.getRole()  + "\"}"
                );
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(@RequestBody UserData userData){

        UserData u = queryRepoMapper.getUserByUsername(userData.getUsername());
        if(u != null && notEmpty(u.getUsername())){
            return new ResponseEntity<>("{\"message\": \"Duplication is not allowed\"}", HttpStatus.FORBIDDEN);
        }

        HashMap<String, String> validate = validateField(userData);
        if(!validate.isEmpty()){
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

        return new ResponseEntity<>("{\"message\":\"Created\"}", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/editUser", method = RequestMethod.POST)
    public ResponseEntity<Object> editUser(@RequestBody UserData userData){
        HashMap<String,String> message = validateField(userData);
        if(!message.isEmpty()){
            return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
        }

//        String password = userData.getPassword();
//        password = passwordEncoder.encode(password);
//        userData.setPassword(password);

        if(queryRepoMapper.updateUser(userData) != 1){
            return new ResponseEntity<>("{\"message\": \"Error\"}", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("{\"message\":\"Updated\"}", HttpStatus.OK);

    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteUsers(@RequestParam String username){

        if(queryRepoMapper.deleteUser(username) != 1){
            return new ResponseEntity<>("{\"message\": \"Error\"}", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("{message: " + username + " deleted}", HttpStatus.OK);
    }

    /** WORK IN PROGRESS **/
    @RequestMapping(value = "/deleteBulk", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteUsersBulk(@RequestParam String username){
        System.out.println(username);

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

        if(!notEmpty(d.getEmail())){
            erroMessage.put("email","Fill in email");
        }

        return erroMessage;
    }

    private boolean notEmpty(String value){
        return value != null && !value.isEmpty();
    }

}
