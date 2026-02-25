package org.example.reminder1.service;

import lombok.RequiredArgsConstructor;
import org.example.reminder1.entity.User;
import org.example.reminder1.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void createUser(User user) {
        userRepository.save(user);
    }

    public User getUserByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new RuntimeException("User не найден"));
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }
}