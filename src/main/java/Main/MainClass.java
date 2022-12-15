package Main;

import controller.User;
import dto.UserData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/v1")
public class MainClass {

    public static void main(String[] args) {
        SpringApplication.run(MainClass.class, args);
    }

    private static Map<String, UserData> userDataRepo = new HashMap<>();
    static {
        UserData user = new UserData();
        user.setUsername("admin");
        user.setFirstName("Admin");
        user.setLastName("Hehehehe");

        userDataRepo.put(user.getUsername(), user);

        user = new UserData();
        user.setUsername("harleyheh");
        user.setFirstName("Harley");
        user.setLastName("Hohohoho");

        userDataRepo.put(user.getUsername(), user);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<Object> getUsers(){
        return new ResponseEntity<>(userDataRepo.values(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getUserByUsername", method = RequestMethod.GET)
    public ResponseEntity<Object> getUserByUsername(@RequestParam String username){
        UserData u = userDataRepo.get(username);
        Map<String, UserData> uRepo = new HashMap<>();
        uRepo.put(u.getUsername(), u);
        return new ResponseEntity<>(uRepo.values(), HttpStatus.OK);
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(@RequestBody UserData userData){

        if(userDataRepo.containsKey(userData.getUsername())){
            return new ResponseEntity<>("{\"error\": \"Duplication is not allowed\"}", HttpStatus.FORBIDDEN);
        }

        UserData user = new UserData();
        user.setUsername(userData.getUsername());
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        userDataRepo.put(user.getUsername(), user);
        return new ResponseEntity<>(userDataRepo.values(), HttpStatus.OK);
    }

    @RequestMapping(value = "/editUser", method = RequestMethod.POST)
    public ResponseEntity<Object> editUser(@RequestBody UserData userData){
        HashMap<String,String> message = validateField(userData);
        if(message != null){
            return new ResponseEntity<>(message.values(), HttpStatus.FORBIDDEN);
        }

        if(userDataRepo.containsKey(userData.getUsername())){
            UserData u = new UserData();
            u.setUserData(userData.getUsername(), userData.getPassword(), userData.getFirstName(), userData.getLastName(), userData.getRole());
            userDataRepo.put(u.getUsername(), u);
        }


        return new ResponseEntity<>(userDataRepo.values(), HttpStatus.OK);

    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResponseEntity<Object> login(@RequestBody UserData userData){
        System.out.println("Logging in");
        return new ResponseEntity<>(userDataRepo.values(), HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteUsers(@RequestParam String username){
        System.out.println("username to be deleted: " + username);
        return new ResponseEntity<>(userDataRepo.values(), HttpStatus.OK);
    }

    private HashMap<String, String> validateField(UserData d){

        HashMap<String,String> erroMessage = new HashMap<>();

        if(!notEmpty(d.getUsername())){
            erroMessage.put("username","Fill in username");
        }

        if(!notEmpty(d.getFirstName())){
            erroMessage.put("firstname","Fill in first name");
        }

        if(!notEmpty(d.getLastName())){
            erroMessage.put("lastname","Fill in lastname");
        }


        return null;
    }

    private boolean notEmpty(String value){
        return value != null && !value.isEmpty();
    }
}
