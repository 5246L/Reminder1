package org.example.reminder1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.reminder1.dto.UpdateEmailRequest;
import org.example.reminder1.dto.UpdateTelegramRequest;
import org.example.reminder1.dto.UserProfileResponse;
import org.example.reminder1.entity.User;
import org.example.reminder1.mapper.UserMapper;
import org.example.reminder1.repository.UserRepository;
import org.example.reminder1.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private User getUserFromOAuth2(OAuth2User oauth2User) {
        String googleId = oauth2User.getAttribute("sub");
        return userService.getUserByGoogleId(googleId);
    }

    @GetMapping("/profile")
    public UserProfileResponse getProfile(@AuthenticationPrincipal OAuth2User oauth2User) {
        User user = getUserFromOAuth2(oauth2User);
        return userMapper.toResponse(user);
    }

    @PutMapping("/profile/email")
    public UserProfileResponse updateEmail(
            @Valid @RequestBody UpdateEmailRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        User user = getUserFromOAuth2(oauth2User);
        user.setEmail(request.getEmail());
        User updated = userService.updateUser(user);

        return userMapper.toResponse(updated);
    }

    @PutMapping("/profile/telegram")
    public UserProfileResponse updateTelegram(
            @Valid @RequestBody UpdateTelegramRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        User user = getUserFromOAuth2(oauth2User);
        user.setTelegramChatId(request.getTelegramChatId());
        User updated = userService.updateUser(user);

        return userMapper.toResponse(updated);
    }

    @DeleteMapping("/profile/telegram")
    public UserProfileResponse removeTelegram(@AuthenticationPrincipal OAuth2User oauth2User) {
        User user = getUserFromOAuth2(oauth2User);

        user.setTelegramChatId(null);
        User updated = userService.updateUser(user);

        return userMapper.toResponse(updated);
    }

    @PutMapping("/test/telegram")
    public UserProfileResponse testUpdateTelegram(
            @Valid @RequestBody UpdateTelegramRequest updateTelegramRequest,
            @RequestParam Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User с ID " + userId + " не найден"));

        user.setTelegramChatId(updateTelegramRequest.getTelegramChatId());
        User updated = userRepository.save(user);

        return userMapper.toResponse(updated);
    }
}