package com.example.resumeanalyzer.service;

import com.example.resumeanalyzer.entity.User;
import com.example.resumeanalyzer.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Test
    void registerUserShouldHashPasswordBeforeSaving() {
        UserService service = new UserService();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ReflectionTestUtils.setField(service, "userRepository", userRepository);

        User user = new User();
        user.setEmail("demo@example.com");
        user.setPassword("secret123");
        user.setRole("USER");

        User saved = service.registerUser(user);

        assertNotEquals("secret123", saved.getPassword());
        assertTrue(saved.getPassword().startsWith("$2a$") || saved.getPassword().startsWith("$2b$"));
    }
}
