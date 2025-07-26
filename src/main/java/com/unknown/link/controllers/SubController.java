package com.unknown.link.controllers;

import com.unknown.link.dtos.SubDTO;
import com.unknown.link.dtos.UserDTO;
import com.unknown.link.dtos.UserListDTO;
import com.unknown.link.entities.User;
import com.unknown.link.services.SubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "Subscribe Controller", description = "Контроллер для работы подписками.")
@RequestMapping("/subscribe")
@AllArgsConstructor
public class SubController {
    private final SubService subService;

    @Operation(summary = "Get subscribes by user id", description = "Возвращает все подписки пользователя.")
    @GetMapping("/subs")
    public UserListDTO getSubs(@RequestParam String user_id) {
        log.debug("Get subscribes of {}", user_id);
        return subService.getSubscribes(user_id);
    }

    @Operation(summary = "Get subscribers by user id", description = "Возвращает всех подписчиков пользователя.")
    @GetMapping("/subers")
    public UserListDTO getSubers(@RequestParam String sub_id) {
        log.debug("Get subscribers of {}", sub_id);
        return subService.getSubscribers(sub_id);
    }

    @Operation(summary = "Debug endpoint", description = "Отладочный эндпоинт: возвращает всех пользователей")
    @GetMapping("/user")
    public List<User> getUser() {
        log.debug("Create user with id ");
        return subService.getUser();
    }

    @Operation(summary = "Add new user", description = "Добавляет нового пользователя.")
    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody UserDTO data) {
        log.debug("Create user with id {}", data);
        subService.addUser(data.userId());
    }

    @Operation(summary = "Add new subscribe", description = "Добавляет подписку.")
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public void createSub(@RequestBody SubDTO data) {
        log.debug("Create sub from {} to {}", data.user_id(), data.sub_id());
        subService.addSubscribe(data.user_id(), data.sub_id());
    }

    @Operation(summary = "Delete subscribe", description = "Удаляет подписку.")
    @DeleteMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSub(@RequestBody SubDTO data) {
        log.debug("Delete sub from {} to {}", data.user_id(), data.sub_id());
        subService.delSubscribe(data.user_id(), data.sub_id());
    }

    @Operation(summary = "Delete user data", description = "Удаляет пользователя по имени.")
    @DeleteMapping("/user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delUser(@RequestBody UserDTO data) {
        log.debug("Delete user by id {}", data);
        subService.delUser(data.userId());
    }
}
