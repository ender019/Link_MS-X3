package com.unknown.link.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unknown.link.controllers.SubController;
import com.unknown.link.dtos.GUserDTO;
import com.unknown.link.dtos.SubDTO;
import com.unknown.link.dtos.UserDTO;
import com.unknown.link.dtos.UserListDTO;
import com.unknown.link.entities.User;
import com.unknown.link.services.SubService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(SubController.class)
public class SubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubService subService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Test
    public void getSubsTest() throws Exception {
        String userId = "user123";

        List<GUserDTO> subscribers = List.of(
                new GUserDTO("sub1", "username1", "email1@mail.ru", "desc", "ava.png", LocalDateTime.now()),
                new GUserDTO("sub2", "username2", "email2@mail.ru", "desc", "ava.png", LocalDateTime.now())
        );

        Mockito.when(subService.getSubscribes(userId)).thenReturn(new UserListDTO(subscribers));

        mockMvc.perform(get("/subscribe/subs")
                        .param("user_id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users", hasSize(2)))
                .andExpect(jsonPath("$.users[0].id").value("sub1"))
                .andExpect(jsonPath("$.users[1].id").value("sub2"));

        Mockito.verify(subService, Mockito.times(1)).getSubscribes(userId);
    }

    @Test
    public void getSubersTest() throws Exception {
        String subId = "sub456";
        List<GUserDTO> subscribers = List.of(
                new GUserDTO("sub1", "username1", "email1@mail.ru", "desc", "ava.png", LocalDateTime.now()),
                new GUserDTO("sub2", "username2", "email2@mail.ru", "desc", "ava.png", LocalDateTime.now())
        );

        Mockito.when(subService.getSubscribers(subId)).thenReturn(new UserListDTO(subscribers));

        mockMvc.perform(get("/subscribe/subers")
                        .param("sub_id", subId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users", hasSize(2)))
                .andExpect(jsonPath("$.users[0].id").value("sub1"))
                .andExpect(jsonPath("$.users[1].id").value("sub2"));

        Mockito.verify(subService, Mockito.times(1)).getSubscribers(subId);
    }

    @Test
    public void getUserTest() throws Exception {
        List<User> users = List.of(
                new User("user1"),
                new User("user2")
        );
        users.get(0).setId(1L);
        users.get(1).setId(2L);

        Mockito.when(subService.getUser()).thenReturn(users);

        mockMvc.perform(get("/subscribe/user"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        Mockito.verify(subService, Mockito.times(1)).getUser();
    }

    @Test
    public void createUserTest() throws Exception {
        UserDTO userDTO = new UserDTO("newUser");
        String requestBody = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/subscribe/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        Mockito.verify(subService, Mockito.times(1)).addUser("newUser");
    }

    @Test
    public void createSubTest() throws Exception {
        SubDTO subDTO = new SubDTO("user789", "sub999");
        String requestBody = objectMapper.writeValueAsString(subDTO);

        mockMvc.perform(post("/subscribe/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        Mockito.verify(subService, Mockito.times(1)).addSubscribe("user789", "sub999");
    }

    @Test
    public void deleteSubTest() throws Exception {
        SubDTO subDTO = new SubDTO("user111", "sub222");
        String requestBody = objectMapper.writeValueAsString(subDTO);

        mockMvc.perform(delete("/subscribe/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());

        Mockito.verify(subService, Mockito.times(1)).delSubscribe("user111", "sub222");
    }

    @Test
    public void delUserTest() throws Exception {
        UserDTO userDTO = new UserDTO("userToDelete");
        String requestBody = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(delete("/subscribe/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());

        Mockito.verify(subService, Mockito.times(1)).delUser("userToDelete");
    }
}
