package controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import dto.UserData;

import java.util.HashMap;
import java.util.Map;

@RestController
public class User {

    private static final Map<String, UserData> userDataRepo = new HashMap<>();
    static {
        UserData user = new UserData();
        user.setFirstName("Amirul");
        user.setLastName("Aiman");
    }

    @RequestMapping(value = "/user")
    public ResponseEntity<Object> getUsers(){
        return new ResponseEntity<>(userDataRepo.values(), HttpStatus.OK);
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(@RequestBody UserData userData){
        return null;
    }



}
