package org.example.reminder1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.reminder1.dto.FilterRequest;
import org.example.reminder1.dto.ReminderCreateRequest;
import org.example.reminder1.dto.ReminderResponse;
import org.example.reminder1.dto.SortRequest;
import org.example.reminder1.service.ReminderService;
import org.example.reminder1.util.OAuth2UserResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reminder")
@RequiredArgsConstructor
public class ReminderController {
    private final ReminderService reminderService;
    private final OAuth2UserResolver oAuth2UserResolver;


    @PostMapping("/create")
    public ReminderResponse createReminder(
            @Valid @RequestBody ReminderCreateRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        return reminderService.create(request, oAuth2UserResolver.resolve(oauth2User));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteReminder(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        reminderService.delete(id, oAuth2UserResolver.resolve(oauth2User));
        return ResponseEntity.ok("Напоминание удалено");
    }

    @PutMapping("/update")
    public ReminderResponse updateReminder(
            @Valid @RequestBody ReminderCreateRequest request,
            @RequestParam Long id,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        return reminderService.update(id, request, oAuth2UserResolver.resolve(oauth2User));
    }

    @GetMapping("/list")
    public List<ReminderResponse> listReminder(@AuthenticationPrincipal OAuth2User oauth2User) {

        return reminderService.list(oAuth2UserResolver.resolve(oauth2User));

    }

    @PostMapping("/sort")
    public List<ReminderResponse> sortReminder(
            @Valid @RequestBody SortRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        return reminderService.sort(request, oAuth2UserResolver.resolve(oauth2User));
    }

    @PostMapping("/filter")
    public List<ReminderResponse> filterReminder(
            @Valid @RequestBody FilterRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        return reminderService.filter(request, oAuth2UserResolver.resolve(oauth2User));
    }
}
