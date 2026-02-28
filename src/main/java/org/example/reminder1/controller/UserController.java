package org.example.reminder1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.reminder1.dto.UpdateEmailRequest;
import org.example.reminder1.dto.UpdateTelegramRequest;
import org.example.reminder1.dto.UserProfileResponse;
import org.example.reminder1.service.UserService;
import org.example.reminder1.util.OAuth2UserResolver;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final OAuth2UserResolver oAuth2UserResolver;

    @GetMapping("/profile")
    public UserProfileResponse getProfile(@AuthenticationPrincipal OAuth2User oauth2User) {
        return userService.getProfile(oAuth2UserResolver.resolve(oauth2User));
    }

    @PutMapping("/profile/email")
    public UserProfileResponse updateEmail(
            @Valid @RequestBody UpdateEmailRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {
        return userService.updateEmail(request, oAuth2UserResolver.resolve(oauth2User));
    }

    @PutMapping("/profile/telegram")
    public UserProfileResponse updateTelegram(
            @Valid @RequestBody UpdateTelegramRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        return userService.updateTelegram(request, oAuth2UserResolver.resolve(oauth2User));
    }

    @DeleteMapping("/profile/telegram")
    public UserProfileResponse removeTelegram(@AuthenticationPrincipal OAuth2User oauth2User) {
        return userService.removeTelegram(oAuth2UserResolver.resolve(oauth2User));
    }
}