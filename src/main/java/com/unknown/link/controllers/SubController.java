package com.unknown.link.controllers;

import com.unknown.link.dtos.SubDTO;
import com.unknown.link.dtos.UserDTO;
import com.unknown.link.dtos.UserListDTO;
import com.unknown.link.entities.User;
import com.unknown.link.services.SubService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/subscribe")
@AllArgsConstructor
public class SubController {
    private final SubService subService;

    @GetMapping("/subs")
    public UserListDTO getSubs(@RequestParam String user_id) {
        log.debug("Get subscribes of {}", user_id);
        return subService.getSubscribes(user_id);
    }

    @GetMapping("/subers")
    public UserListDTO getSubers(@RequestParam String sub_id) {
        log.debug("Get subscribers of {}", sub_id);
        return subService.getSubscribers(sub_id);
    }

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public List<User> getUser() {
        log.debug("Create user with id ");
        return subService.getUser();
    }

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody UserDTO data) {
        log.debug("Create user with id {}", data);
        subService.addUser(data.userId());
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public void createSub(@RequestBody SubDTO data) {
        log.debug("Create sub from {} to {}", data.user_id(), data.sub_id());
        subService.addSubscribe(data.user_id(), data.sub_id());
    }

    @DeleteMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSub(@RequestBody SubDTO data) {
        log.debug("Delete sub from {} to {}", data.user_id(), data.sub_id());
        subService.delSubscribe(data.user_id(), data.sub_id());
    }

    @DeleteMapping("/user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delUser(@RequestBody UserDTO data) {
        log.debug("Delete user by id {}", data);
        subService.delUser(data.userId());
    }
}
