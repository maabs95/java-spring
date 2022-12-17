package main.controller;

import jakarta.annotation.Resource;
import main.database.QueryRepoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import main.dto.UserData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/v1")
public class User {

    @Resource(name="main.database.QueryRepoMapper")
    QueryRepoMapper queryRepoMapper;

    private Map<String, UserData> returnValue = new HashMap<>();

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<Object> getUsers(){
        List<UserData> getResult = queryRepoMapper.getUserList();
        return new ResponseEntity<>(getResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/getUserByUsername", method = RequestMethod.GET)
    public ResponseEntity<Object> getUserByUsername(@RequestParam String username){
        return new ResponseEntity<>(queryRepoMapper.getUserByUsername(username), HttpStatus.OK);
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

        if(queryRepoMapper.insertUser(userData) != 1){
            return new ResponseEntity<>("{\"message\": \"Error\"}", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userData, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/editUser", method = RequestMethod.POST)
    public ResponseEntity<Object> editUser(@RequestBody UserData userData){
        HashMap<String,String> message = validateField(userData);
        if(message != null){
            return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
        }

        if(queryRepoMapper.updateUser(userData) != 1){
            return new ResponseEntity<>("{\"message\": \"Error\"}", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userData, HttpStatus.OK);

    }
//
//    @RequestMapping(value = "/login",method = RequestMethod.POST)
//    public ResponseEntity<Object> login(@RequestBody UserData userData){
//        System.out.println("Logging in");
//        return new ResponseEntity<>(userDataRepo.values(), HttpStatus.OK);
//    }
//
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
