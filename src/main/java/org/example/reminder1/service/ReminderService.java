package org.example.reminder1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.reminder1.dto.FilterRequest;
import org.example.reminder1.dto.ReminderCreateRequest;
import org.example.reminder1.dto.ReminderResponse;
import org.example.reminder1.dto.SortRequest;
import org.example.reminder1.entity.Reminder;
import org.example.reminder1.entity.User;
import org.example.reminder1.mapper.ReminderMapper;
import org.example.reminder1.repository.ReminderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {
    private final ReminderRepository reminderRepository;
    private final ReminderMapper reminderMapper;

    public List<Reminder> getUserReminder(Long userId) {
        return reminderRepository.findAllByUserId(userId);
    }

    public Reminder getReminderById(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Напоминание не найдено"));
    }

    public List<Reminder> getUserRemindersSorted(Long userId, Sort sort) {
        return reminderRepository.findAllByUserId(userId, sort);
    }

    public List<Reminder> getUserRemindersFiltered(Long userId, LocalDateTime from, LocalDateTime to) {
        return reminderRepository.findByUserIdAndOptionalDateRange(userId, from, to);
    }

    public List<Reminder> getRemindersToSend(LocalDateTime now) {
        return reminderRepository.findAllByRemindBeforeAndNotifiedFalse(now);
    }

    public Reminder createReminder(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    public void deleteReminder(Long id) {
        reminderRepository.deleteById(id);
    }

    public Reminder updateReminder(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    public ReminderResponse create(ReminderCreateRequest request, User user) {
        Reminder reminder = reminderMapper.toEntity(request, user);
        return reminderMapper.toResponse(createReminder(reminder));

    }

    public void delete(Long id, User user) {
        Reminder reminder = getReminderById(id);

        if (!reminder.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Нельзя удалить чужое напоминание");
        }
        deleteReminder(id);
    }

    public ReminderResponse update(Long id, ReminderCreateRequest request, User user) {

        Reminder reminder = getReminderById(id);

        if (!reminder.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нельзя редактировать чужое напоминание");
        }

        reminderMapper.updateEntity(reminder, request);
        return reminderMapper.toResponse(updateReminder(reminder));
    }

    public List<ReminderResponse> list(User user) {
        return getUserReminder(user.getId())
                .stream()
                .map(reminderMapper::toResponse)
                .toList();
    }

    public List<ReminderResponse> sort(SortRequest request, User user) {

        Sort sort = request.getOrder().equalsIgnoreCase("desc")
                ? Sort.by(request.getBy()).descending()
                : Sort.by(request.getBy()).ascending();

        return getUserRemindersSorted(user.getId(), sort)
                .stream()
                .map(reminderMapper::toResponse)
                .toList();
    }

    public List<ReminderResponse> filter(FilterRequest request, User user) {

        return getUserRemindersFiltered(user.getId(), request.getDateFrom(), request.getDateTo())
                .stream()
                .map(reminderMapper::toResponse)
                .toList();
    }

    public void markAsNotified(Reminder reminder) {
        try {
            reminder.setNotified(true);
            reminderRepository.save(reminder);
            log.info("Напоминание ID {} помечено как отправленное", reminder.getId());
        } catch (Exception e) {
            log.error("Ошибка обновления notified для ID {}: {}", reminder.getId(), e.getMessage());
        }
    }
}
