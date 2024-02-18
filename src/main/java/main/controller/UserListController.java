package main.controller;

import main.dto.UserData;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class UserListController {

    @RequestMapping(value = "/getUserListPagination",method = RequestMethod.GET)
    public ResponseEntity<Object> getUserListPagination(@RequestBody UserData userData) throws Exception{
        return ResponseEntity.ok().contentType(MediaType.valueOf("application/json"))
//                .header("Access-Control-Allow-Origin","http://localhost:3000")
                .body("{\"getUserListPagination\":\"" + userData.getRole()  + "\"}"
                );
    }

}
