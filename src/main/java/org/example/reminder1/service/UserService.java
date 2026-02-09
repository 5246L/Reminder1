package org.example.reminder1.service;

import lombok.RequiredArgsConstructor;
import org.example.reminder1.entity.User;
import org.example.reminder1.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUserByTelegramChatId(Long telegramChatId) {
        Optional<User> userTelegramChat = userRepository.findByTelegramChatId(telegramChatId);

        if (userTelegramChat.isPresent()) {
            return userTelegramChat.get();
        } else {
            throw new RuntimeException("Пользователь не найден");
        }
    }

    public User updateTelegramChatId(Long userId, Long telegramChatId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User не найден"));
        user.setTelegramChatId(telegramChatId);
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        Optional<User> userEmail = userRepository.findByEmail(email);

        if (userEmail.isPresent()) {
            return userEmail.get();
        } else {
            throw new RuntimeException("Email не найден");
        }
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
