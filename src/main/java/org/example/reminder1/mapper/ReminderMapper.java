package org.example.reminder1.mapper;

import org.example.reminder1.dto.ReminderCreateRequest;
import org.example.reminder1.dto.ReminderResponse;
import org.example.reminder1.entity.Reminder;
import org.example.reminder1.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ReminderMapper {

    public Reminder toEntity(ReminderCreateRequest request, User user) {
        Reminder reminder = new Reminder();
        reminder.setTitle(request.getTitle());
        reminder.setDescription(request.getDescription());
        reminder.setRemind(request.getRemind());
        reminder.setUser(user);
        reminder.setNotified(false);

        return reminder;
    }

    public ReminderResponse toResponse(Reminder reminder) {
        return new ReminderResponse(
                reminder.getId(),
                reminder.getTitle(),
                reminder.getDescription(),
                reminder.getRemind(),
                reminder.getNotified()
        );
    }

    public void updateEntity(Reminder reminder, ReminderCreateRequest request) {
        reminder.setTitle(request.getTitle());
        reminder.setDescription(request.getDescription());
        reminder.setRemind(request.getRemind());
    }
}
