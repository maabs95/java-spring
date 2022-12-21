package main.controller;

import main.config.SecurityConfig;
import main.database.QueryRepoMapper;
import main.dto.UserData;
import main.service.CustomAuthentication;
import main.utils.JwtUtils;
import org.h2.engine.User;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
//        (
//        properties = "spring.main.lazy-initialization=true",
//        classes = {UserController.class}
//)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
//@ContextConfiguration(classes = { Authentication.class })
//@ContextConfiguration
//@ExtendWith(SpringExtension.class)
//@Import(QueryRepoMapper.class)
class UserControllerTest {

    /**
     * For this test, I have problems setting up some tests. Hence, I ignore it for the time being due to time constraint
     * **/

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private CustomAuthentication customAuthentication;

    @Mock
    UserController userController;

    @Mock
    JwtUtils jwtUtils;

    private static HashMap<String, UserData> mockUsers = new HashMap<>();
    static {
        UserData mockUser1 = new UserData();
        mockUser1.setUsername("admin");
        mockUser1.setFirstname("Admin");
        mockUser1.setLastname("Testing");
        mockUser1.setPassword("$2a$10$8OUlK1DSGH042U1qq5EvueRSa5LEb1eKKAcmLFeQsxFegPmyd74yq");
        mockUser1.setEmail("admin@test.com");
        mockUser1.setRole("ROLE_ADMIN");
        mockUsers.put(mockUser1.getUsername(), mockUser1);

        UserData mockUser2 = new UserData();
        mockUser2.setUsername("amoy");
        mockUser2.setFirstname("Peasant");
        mockUser2.setLastname("Testing");
        mockUser2.setPassword("$2a$10$8OUlK1DSGH042U1qq5EvueRSa5LEb1eKKAcmLFeQsxFegPmyd74yq");
        mockUser2.setEmail("peasant@test.com");
        mockUser2.setRole("ROLE_USER");
        mockUsers.put(mockUser2.getUsername(), mockUser2);

    }

    @BeforeEach
    public void setUp(
//            WebApplicationContext wac
    ) throws Exception {
//        mvc = MockMvcBuilders
//                .webAppContextSetup(webApplicationContext)
//                .apply(springSecurity())
//                .build();
//        this.mvc = MockMvcBuilders.standaloneSetup(new UserController()).build();
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /** WORK IN PROGRESS **/
//    @Test
//    void loginApi() throws Exception {
//        UserData u = mockUsers.get("admin");
//        MvcResult result = mvc.perform( MockMvcRequestBuilders
//                        .post("/v1/login")
//                        .content("{\"username\":\"" + u.getUsername() + "\"," +
//                                "\"password\":\"" + u.getPassword() + "\"}")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.Authorization").exists()).andReturn();
//
//        System.out.println("test response");
//        System.out.println(result.getResponse().getContentAsString());
//    }

    @Test
    void getUsers_ByAdmin() throws Exception {

        UserData u = mockUsers.get("admin");
        Authentication authObject = customAuthentication.authenticate(new UsernamePasswordAuthenticationToken(u.getUsername(),u.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authObject);
        String jwt = jwtUtils.generateJwtToken(authObject);
        mvc.perform( MockMvcRequestBuilders
                        .get("/v1/user").header("Authorization","Bearer " + jwt)
//                        .content("{\"Authorization\":\"Bearer " + jwt+ "\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void getUserByUsername_ByAdmin() throws Exception {
        UserData u = mockUsers.get("admin");
        Authentication authObject = customAuthentication.authenticate(new UsernamePasswordAuthenticationToken(u.getUsername(),u.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authObject);
        String jwt = jwtUtils.generateJwtToken(authObject);
        mvc.perform( MockMvcRequestBuilders
                        .get("/v1/getUserByUsername?username=admin").header("Authorization","Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /** WORK IN PROGRESS **/
//    @Test
//    void loggedInUser() throws Exception {
//        UserData u = mockUsers.get("admin");
//        Authentication authObject = customAuthentication.authenticate(new UsernamePasswordAuthenticationToken(u.getUsername(),u.getPassword()));
//        SecurityContextHolder.getContext().setAuthentication(authObject);
//
//        String jwt = jwtUtils.generateJwtToken(authObject);
//        mvc.perform( MockMvcRequestBuilders
//                        .get("/v1/loggedInUser").header("Authorization","Bearer " + jwt)
//                        .content("{\"Authorization\":\"Bearer " + jwt+ "\"}")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }

    /** WORK IN PROGRESS **/
//    @org.junit.jupiter.api.Test
//    void createUser() {
//    }

    @Test
    void editUser_ByAdmin() throws Exception {
        UserData u = mockUsers.get("admin");
        Authentication authObject = customAuthentication.authenticate(new UsernamePasswordAuthenticationToken(u.getUsername(),u.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authObject);
        String jwt = jwtUtils.generateJwtToken(authObject);
        mvc.perform( MockMvcRequestBuilders
                        .post("/v1/editUser").header("Authorization","Bearer " + jwt)
                        .content("{\"username\":\"" + u.getUsername() + "\","
                                + "\"firstname\":\"" + u.getFirstname() + "\","
                                + "\"lastname\":\"" + u.getLastname() + "\","
                                + "\"role\":\"" + u.getRole() + "\","
                                + "\"email\":\"testChange@email.com\""
                                + "}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUsers_ByAdmin() throws Exception {
        UserData u = mockUsers.get("admin");
        Authentication authObject = customAuthentication.authenticate(new UsernamePasswordAuthenticationToken(u.getUsername(),u.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authObject);
        String jwt = jwtUtils.generateJwtToken(authObject);
        mvc.perform( MockMvcRequestBuilders
                        .delete("/v1/deleteUser?username=amoy").header("Authorization","Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}