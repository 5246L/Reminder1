package org.example.reminder1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.reminder1.dto.FilterRequest;
import org.example.reminder1.dto.ReminderCreateRequest;
import org.example.reminder1.dto.ReminderResponse;
import org.example.reminder1.dto.SortRequest;
import org.example.reminder1.entity.Reminder;
import org.example.reminder1.entity.User;
import org.example.reminder1.repository.ReminderRepository;
import org.example.reminder1.service.ReminderService;
import org.example.reminder1.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
    private final ReminderRepository reminderRepository;
    private final UserService userService;

    private User getUserFromOAuth2(OAuth2User oauth2User) {
        String googleId = oauth2User.getAttribute("sub");
        return userService.getUserByGoogleId(googleId);
    }

    @PostMapping("/create")
    public ReminderResponse createReminder(
            @Valid @RequestBody ReminderCreateRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User
            ) {

        User user = getUserFromOAuth2(oauth2User);

        Reminder reminder = new Reminder();
        reminder.setTitle(request.getTitle());
        reminder.setDescription(request.getDescription());
        reminder.setRemind(request.getRemind());
        reminder.setUser(user);
        reminder.setNotified(false);

        Reminder saved = reminderService.createReminder(reminder);

        return new ReminderResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getRemind(),
                saved.getNotified()
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteReminder(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        User user = getUserFromOAuth2(oauth2User);

        Reminder reminder = reminderRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Напоминание не найдено"));

        if(!reminder.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Нельзя удалить чужое напоминание");
        }

        reminderService.deleteReminder(id);

        return ResponseEntity.ok("Напоминание удалено");
    }

    @PutMapping("/update")
    public ReminderResponse updateReminder(
            @Valid @RequestBody ReminderCreateRequest request,
            @RequestParam Long id,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        User user = getUserFromOAuth2(oauth2User);

        Reminder reminder = new Reminder();
        reminder.setId(id);
        reminder.setTitle(request.getTitle());
        reminder.setDescription(request.getDescription());
        reminder.setRemind(request.getRemind());
        reminder.setUser(user);

        Reminder updated = reminderService.updateReminder(reminder);

        return new ReminderResponse(
                updated.getId(),
                updated.getTitle(),
                updated.getDescription(),
                updated.getRemind(),
                updated.getNotified()
        );
    }

    @GetMapping("/list")
    public List<ReminderResponse> listReminder(@AuthenticationPrincipal OAuth2User oauth2User) {

        User user = getUserFromOAuth2(oauth2User);

        List<Reminder> reminders = reminderService.getUserReminder(user.getId());

        return reminders.stream()
                .map(reminder -> new ReminderResponse(
                        reminder.getId(),
                        reminder.getTitle(),
                        reminder.getDescription(),
                        reminder.getRemind(),
                        reminder.getNotified()
                ))
                .toList();

    }

    @PostMapping("/sort")
    public List<ReminderResponse> sortReminder(
            @Valid @RequestBody SortRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        User user = getUserFromOAuth2(oauth2User);

        Sort sort = request.getOrder().equalsIgnoreCase("desc")
                ? Sort.by(request.getBy()).descending()
                : Sort.by(request.getBy()).ascending();

        List<Reminder> reminders = reminderService.getUserRemindersSorted(user.getId(), sort);

        return reminders.stream()
                .map(reminder -> new ReminderResponse(
                        reminder.getId(),
                        reminder.getTitle(),
                        reminder.getDescription(),
                        reminder.getRemind(),
                        reminder.getNotified()
                ))
                .toList();
    }

    @PostMapping("/filter")
    public List<ReminderResponse> filterReminder(
            @Valid @RequestBody FilterRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        User user = getUserFromOAuth2(oauth2User);

        List<Reminder> reminders = reminderService.getUserRemindersFiltered(
                user.getId(),
                request.getDateFrom(),
                request.getDateTo()
        );

        return reminders.stream()
                .map(reminder -> new ReminderResponse(
                        reminder.getId(),
                        reminder.getTitle(),
                        reminder.getDescription(),
                        reminder.getRemind(),
                        reminder.getNotified()
                ))
                .toList();
    }
}
