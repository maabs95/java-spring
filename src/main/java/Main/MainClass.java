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
        user.setFirstName("Amirul");
        user.setLastName("Aiman");

        userDataRepo.put(user.getFirstName(), user);

        user = new UserData();
        user.setFirstName("Aida");
        user.setLastName("Izzati");

        userDataRepo.put(user.getFirstName(), user);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<Object> getUsers(){
        return new ResponseEntity<>(userDataRepo.values(), HttpStatus.OK);
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(@RequestBody UserData userData){
        UserData user = new UserData();
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        userDataRepo.put(user.getFirstName(), user);
        System.out.println(userData.getFirstName() + " // " + userData.getLastName());
        return new ResponseEntity<>(userDataRepo.values(), HttpStatus.OK);
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResponseEntity<Object> login(@RequestBody UserData userData){
        UserData u = new UserData();
        u.setUserData(userData.getUsername(), userData.getPassword(), userData.getFirstName(), userData.getLastName(), userData.getRole());
        userDataRepo = new HashMap<>();
        userDataRepo.put(u.getFirstName(), u);
        return new ResponseEntity<>(userDataRepo.values(), HttpStatus.OK);
    }
}
