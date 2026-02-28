package org.example.reminder1.service;

import lombok.RequiredArgsConstructor;
import org.example.reminder1.dto.*;
import org.example.reminder1.entity.User;
import org.example.reminder1.mapper.UserMapper;
import org.example.reminder1.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

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

    public UserProfileResponse getProfile(User user) {
        return userMapper.toResponse(user);
    }

    public UserProfileResponse updateEmail(UpdateEmailRequest request, User user) {
        user.setEmail(request.getEmail());

        return userMapper.toResponse(updateUser(user));
    }

    public UserProfileResponse updateTelegram(UpdateTelegramRequest request, User user) {
        user.setTelegramChatId(request.getTelegramChatId());

        return userMapper.toResponse(updateUser(user));
    }

    public UserProfileResponse removeTelegram(User user) {
        user.setTelegramChatId(null);

        return userMapper.toResponse(updateUser(user));
    }

    public AuthSuccessResponse getAuthSuccess(OAuth2User oauth2User) {
        return new AuthSuccessResponse(
                "Авторизация успешна!",
                oauth2User.getAttribute("name"),
                oauth2User.getAttribute("email")
        );
    }

    public CurrentUserResponse getCurrentUser(OAuth2User oauth2User) {
        return new CurrentUserResponse(
                oauth2User.getAttribute("name"),
                oauth2User.getAttribute("email"),
                oauth2User.getAttribute("picture")
        );
    }
}