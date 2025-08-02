package com.unknown.link.services;

import com.unknown.link.dtos.GUserDTO;
import com.unknown.link.dtos.UserIdDTO;
import com.unknown.link.dtos.UserListDTO;
import com.unknown.link.entities.Subscribe;
import com.unknown.link.entities.User;
import com.unknown.link.repositories.SubRepository;
import com.unknown.link.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@AllArgsConstructor
public class SubService {
    private final UserRepository userRepository;
    private final SubRepository subRepository;

    private final WebClient webClient;

    private UserListDTO getFullUsers(List<String> userIds) {
        var data = webClient.method(HttpMethod.GET).uri("/user/group")
                .contentType(MediaType.APPLICATION_JSON).bodyValue(new UserIdDTO(userIds))
                .retrieve().bodyToMono(new ParameterizedTypeReference<List<GUserDTO>>() {})
                .doOnSuccess(res -> log.debug("Success: {}", res))
                .doOnError(res -> log.debug("Error: {}", res.getMessage()))
                .block();
        return new UserListDTO(data);
    }

    public UserListDTO getSubscribes(String userId) {
        var user = userRepository.findUserByUserId(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        var userIds = user.getSubs().stream().map(Subscribe::getSubscribe).map(User::getUserId).toList();
        log.debug("User Ids: {}", userIds);
        return getFullUsers(userIds);
    }

    public UserListDTO getSubscribers(String userId) {
        var user = userRepository.findUserByUserId(userId).orElseThrow();
        var userIds = subRepository.findSubscribesBySubId(user.getId()).stream().map(el -> el.get("userId")).toList();
        log.debug("User Ids: {}", userIds);
        return getFullUsers(userIds);
    }

    @Transactional
    public void addSubscribe(String userId, String sub_id) {
        var user = userRepository.findUserByUserId(userId).orElse(
                userRepository.save(new User(userId))
        );
        var sub = userRepository.findUserByUserId(sub_id).orElseThrow(() -> new NoSuchElementException("Sub not found"));
        if (user.getSubs().stream().anyMatch(el -> el.getSubscribe().equals(sub)))
            throw new DataIntegrityViolationException("Subscribe already exists");
        var subs = new Subscribe();
        subs.setUserId(user.getUserId());
        subs.setSubscribe(sub);
        user.getSubs().add(subs);
        userRepository.save(user);
        subRepository.save(subs);
    }

    @Transactional
    public void delSubscribe(String userId, String sub_id) {
        var user = userRepository.findUserByUserId(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        var sub = userRepository.findUserByUserId(sub_id).orElseThrow(() -> new NoSuchElementException("Sub not found"));
        var subs = user.getSubs().stream().filter(el -> el.getSubscribe().equals(sub)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Subscribe not found"));
        subRepository.delete(subs);
        user.getSubs().remove(subs);
        userRepository.save(user);
    }

    public List<User> getUser() {
        return userRepository.findAll();
    }
}
