package com.unknown.link.services;

import com.unknown.link.dtos.GUserDTO;
import com.unknown.link.dtos.UserListDTO;
import com.unknown.link.entities.Subscribe;
import com.unknown.link.entities.User;
import com.unknown.link.repositories.SubRepository;
import com.unknown.link.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class SubServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubRepository subRepository;

    @Mock
    private WebClient webClient;
    
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private SubService subService;

    private final String userId = "user123";
    private final String subId = "sub456";
    private final User user = new User(userId);
    private final User subscribeUser = new User(subId);

    @Test
    void getSubscribes_shouldReturnUserList() {
        // Arrange
        String userId = "user123";
        User user = new User(userId);

        User subUser1 = new User("sub1");
        User subUser2 = new User("sub2");

        Subscribe subscribe1 = new Subscribe();
        subscribe1.setSubscribe(subUser1);
        Subscribe subscribe2 = new Subscribe();
        subscribe2.setSubscribe(subUser2);

        user.setSubs(List.of(subscribe1, subscribe2));

        Mockito.when(userRepository.findUserByUserId(userId))
                .thenReturn(Optional.of(user));

        // Mock WebClient chain
        Mockito.when(webClient.method(any())).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);


        List<GUserDTO> expectedUsers = List.of(
                new GUserDTO("sub1", "username1", "email1@mail.ru", "desc", "ava.png", LocalDateTime.now()),
                new GUserDTO("sub2", "username2", "email2@mail.ru", "desc", "ava.png", LocalDateTime.now())
        );

        Mockito.when(responseSpec.bodyToMono(new ParameterizedTypeReference<List<GUserDTO>>() {}))
                .thenReturn(Mono.just(expectedUsers));

        // Act
        UserListDTO result = subService.getSubscribes(userId);

        // Assert
        Assertions.assertEquals(expectedUsers, result.users());
        Mockito.verify(userRepository).findUserByUserId(userId);
    }

    @Test
    void getSubscribers_shouldReturnSubscribers() {
        // Arrange
        String userId = "user123";
        User user = new User(userId);
        user.setId(1L);

        Mockito.when(userRepository.findUserByUserId(userId))
                .thenReturn(Optional.of(user));

        List<Map<String, String>> subResults = List.of(
                Map.of("userId", "sub1"),
                Map.of("userId", "sub2")
        );

        Mockito.when(subRepository.findSubscribesBySubId(user.getId()))
                .thenReturn(subResults);

        // Mock WebClient chain
        Mockito.when(webClient.method(any())).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        List<GUserDTO> expectedUsers = List.of(
                new GUserDTO("sub1", "username1", "email1@mail.ru", "desc", "ava.png", LocalDateTime.now()),
                new GUserDTO("sub2", "username2", "email2@mail.ru", "desc", "ava.png", LocalDateTime.now())
        );

        Mockito.when(responseSpec.bodyToMono(new ParameterizedTypeReference<List<GUserDTO>>() {}))
                .thenReturn(Mono.just(expectedUsers));

        // Act
        UserListDTO result = subService.getSubscribers(userId);

        // Assert
        Assertions.assertEquals(expectedUsers, result.users());
        Mockito.verify(userRepository).findUserByUserId(userId);
        Mockito.verify(subRepository).findSubscribesBySubId(user.getId());
    }

    @Test
    void getSubscribers_shouldThrowWhenUserNotFound() {
        // Arrange
        String userId = "invalid";
        Mockito.when(userRepository.findUserByUserId(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(NoSuchElementException.class, () -> subService.getSubscribers(userId));
    }

    @Test
    void getSubscribes_shouldThrowWhenUserNotFound() {
        // Arrange
        Mockito.when(userRepository.findUserByUserId(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(NoSuchElementException.class, () -> subService.getSubscribes(userId));
    }

    @Test
    void addSubscribe_NewUser_NewSubscribe_Success() {
        // Arrange
        String userId = "user1";
        String subId = "user2";

        User user = new User(userId);
        User subscribeUser = new User(subId);

        Mockito.when(userRepository.findUserByUserId(userId))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(user)); // Для последующих вызовов
        Mockito.when(userRepository.findUserByUserId(subId))
                .thenReturn(Optional.of(subscribeUser));
        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(user); // Всегда возвращаем сохраненного пользователя

        // Act
        subService.addSubscribe(userId, subId);

        // Assert
        Mockito.verify(userRepository, Mockito.times(2)).save(any(User.class));
        Mockito.verify(subRepository, Mockito.times(1)).save(any(Subscribe.class));
        Assertions.assertTrue(user.getSubs().stream()
                .anyMatch(s -> s.getSubscribe().equals(subscribeUser)));
    }

    @Test
    void addSubscribe_ExistingUser_NewSubscribe_Success() {
        Mockito.when(userRepository.findUserByUserId(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findUserByUserId(subId)).thenReturn(Optional.of(subscribeUser));

        // Act
        subService.addSubscribe(userId, subId);

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verify(subRepository, Mockito.times(1)).save(any(Subscribe.class));
        Assertions.assertTrue(user.getSubs().stream().anyMatch(s -> s.getSubscribe().equals(subscribeUser)));
    }

    @Test
    void addSubscribe_shouldThrowWhenSubscriptionExists() {
        // Arrange
        Subscribe existingSubscribe = new Subscribe();
        existingSubscribe.setSubscribe(subscribeUser);
        user.setSubs(List.of(existingSubscribe));

        Mockito.when(userRepository.findUserByUserId(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.findUserByUserId(subId))
                .thenReturn(Optional.of(subscribeUser));

        // Act & Assert
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> subService.addSubscribe(userId, subId));
    }

    @Test
    void delSubscribe_shouldRemoveSubscription() {
        // Arrange
        Subscribe subscribe = new Subscribe();
        subscribe.setUserId(userId);
        subscribe.setSubscribe(subscribeUser);
        user.setSubs(new ArrayList<>(List.of(subscribe)));

        Mockito.when(userRepository.findUserByUserId(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.findUserByUserId(subId))
                .thenReturn(Optional.of(subscribeUser));

        // Act
        subService.delSubscribe(userId, subId);

        // Assert
        Mockito.verify(subRepository).delete(subscribe);
        Assertions.assertEquals(0, user.getSubs().size());
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void delSubscribe_shouldThrowWhenSubscriptionNotFound() {
        // Arrange
        Mockito.when(userRepository.findUserByUserId(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.findUserByUserId(subId))
                .thenReturn(Optional.of(subscribeUser));

        // Act & Assert
        Assertions.assertThrows(NoSuchElementException.class, () -> subService.delSubscribe(userId, subId));
    }

    @Test
    void getUser_shouldReturnAllUsers() {
        // Arrange
        List<User> users = List.of(new User("u1"), new User("u2"));
        Mockito.when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = subService.getUser();

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Mockito.verify(userRepository).findAll();
    }
}
