package org.example.reminder1.service;

import lombok.RequiredArgsConstructor;
import org.example.reminder1.entity.Reminder;
import org.example.reminder1.repository.ReminderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderService {
    private final ReminderRepository reminderRepository;

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
}
