package com.unknown.link.services;

import com.unknown.link.entities.Subscribe;
import com.unknown.link.entities.User;
import com.unknown.link.repositories.SubRepository;
import com.unknown.link.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@AllArgsConstructor
public class SubService {
    private final UserRepository userRepository;
    private final SubRepository subRepository;

    public List<String> getSubscribes(String userId) {
        var user = userRepository.findUserByUserId(userId).orElseThrow(() -> new NoSuchElementException("User not found"));

        return user.getSubs().stream().map(Subscribe::getSubscribe).map(User::getUserId).toList();
    }

    public List<String> getSubscribers(String userId) {
        var user = userRepository.findUserByUserId(userId).orElseThrow();

        return subRepository.findSubscribesBySubId(user.getId()).stream().map(el -> el.get("userId")).toList();
    }

    @Transactional
    public void addUser(String userId) {
        userRepository.findUserByUserId(userId)
                .ifPresent(_ -> {throw new DataIntegrityViolationException("User already exists");});
        userRepository.save(new User(userId));
    }

    @Transactional
    public void addSubscribe(String userId, String sub_id) {
        var user = userRepository.findUserByUserId(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        var sub = userRepository.findUserByUserId(sub_id).orElseThrow(() -> new NoSuchElementException("User not found"));
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
        var sub = userRepository.findUserByUserId(sub_id).orElseThrow(() -> new NoSuchElementException("User not found"));
        var subs = user.getSubs().stream().filter(el -> el.getSubscribe().equals(sub)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Subscribe not found"));
        subRepository.delete(subs);
        user.getSubs().remove(subs);
        userRepository.save(user);
    }

    public List<User> getUser() {
        return userRepository.findAll();
    }

    @Transactional
    public void delUser(String userId) {
        var user = userRepository.findUserByUserId(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        userRepository.delete(user);
    }
}
