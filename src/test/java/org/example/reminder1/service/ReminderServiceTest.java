package org.example.reminder1.service;

import org.example.reminder1.entity.Reminder;
import org.example.reminder1.entity.User;
import org.example.reminder1.repository.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReminderServiceTest {

    @Mock
    private ReminderRepository reminderRepository;

    @InjectMocks
    private ReminderService reminderService;

    private User testUser;
    private Reminder testReminder;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testReminder = new Reminder();
        testReminder.setId(1L);
        testReminder.setTitle("Test Reminder");
        testReminder.setDescription("Test description");
        testReminder.setRemind(LocalDateTime.now().plusHours(1L));
        testReminder.setNotified(false);
        testReminder.setUser(testUser);
    }

    @Test
    void testCreateReminder() {
        when(reminderRepository.save(any(Reminder.class))).thenReturn(testReminder);

        Reminder result = reminderService.createReminder(testReminder);

        assertNotNull(result);
        assertEquals("Test Reminder", result.getTitle());
        verify(reminderRepository, times(1)).save(testReminder);
    }


    @Test
    void testGetUserRemimders() {

        List<Reminder> reminders = Arrays.asList(testReminder);
        when(reminderRepository.findAllByUserId(1L)).thenReturn(reminders);

        List<Reminder> result = reminderService.getUserReminder(1L);

        assertEquals(1, result.size());
        assertEquals("Test Reminder", result.get(0).getTitle());
        verify(reminderRepository, times(1)).findAllByUserId(1L);

    }

    @Test
    void testGetRemindersToSend() {
        LocalDateTime now = LocalDateTime.now();
        testReminder.setRemind(now.minusMinutes(5));
        List<Reminder> reminders = Arrays.asList(testReminder);
        when(reminderRepository.findAllByRemindBeforeAndNotifiedFalse(any(LocalDateTime.class)))
                .thenReturn(reminders);

        List<Reminder> result = reminderService.getRemindersToSend(now);

        assertEquals(1, result.size());
        assertFalse(result.get(0).getNotified());

    }

    @Test
    void testDeleteReminder() {
        Long remindersId = 1L;
        doNothing().when(reminderRepository).deleteById(remindersId);

       reminderService.deleteReminder(remindersId);

       verify(reminderRepository, times(1)).deleteById(remindersId);

    }

    @Test
    void testUpdateReminder() {
        testReminder.setTitle("Update Title");
        when(reminderRepository.save(any(Reminder.class))).thenReturn(testReminder);

        Reminder result = reminderService.updateReminder(testReminder);

        assertEquals("Update Title", result.getTitle());
        verify(reminderRepository, times(1)).save(testReminder);

    }
}
