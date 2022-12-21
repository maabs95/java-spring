package main.controller;

import lombok.SneakyThrows;
import main.config.SecurityConfig;
import main.dto.UserData;
import main.service.CustomAuthentication;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(
        properties = "spring.main.lazy-initialization=true",
        classes = {UserController.class})
@AutoConfigureMockMvc
//@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

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
            WebApplicationContext wac
    ) throws Exception {
//        mvc = MockMvcBuilders
//                .webAppContextSetup(webApplicationContext)
//                .apply(springSecurity())
//                .build();
//        this.mvc = MockMvcBuilders.standaloneSetup(new UserController()).build();
        this.mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void loginApi() throws Exception {
        UserData u = mockUsers.get("admin");
        MvcResult result = mvc.perform( MockMvcRequestBuilders
                        .post("/v1/login")
                        .content("{\"username\":" + u.getUsername() + "\"," +
                                "\"password\":" + u.getPassword() + "\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.Authorization").exists()).andReturn();

        System.out.println("test response");
        System.out.println(result.getResponse().getContentAsString());
    }

    @org.junit.jupiter.api.Test
    void getUsersAdmin() {

    }

    @org.junit.jupiter.api.Test
    void getUserByUsername() {
    }

    @org.junit.jupiter.api.Test
    void loggedInUser() {
    }

    @org.junit.jupiter.api.Test
    void createUser() {
    }

    @org.junit.jupiter.api.Test
    void editUser() {
    }

    @org.junit.jupiter.api.Test
    void deleteUsers() {
    }

    @org.junit.jupiter.api.Test
    void deleteUsersBulk() {
    }
}