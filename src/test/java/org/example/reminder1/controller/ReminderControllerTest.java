package org.example.reminder1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.reminder1.dto.ReminderCreateRequest;
import org.example.reminder1.entity.Reminder;
import org.example.reminder1.entity.User;
import org.example.reminder1.repository.ReminderRepository;
import org.example.reminder1.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ReminderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setGoogleId("test-google-id");
        testUser.setEmail("test@example.com");
        testUser = userRepository.save(testUser);

    }

    @Test
    void testCreateReminder() throws Exception {
        ReminderCreateRequest request = new ReminderCreateRequest();
        request.setTitle("Test Reminder");
        request.setDescription("Test Description");
        request.setRemind(LocalDateTime.now().plusHours(1));

        mockMvc.perform(post("/api/v1/reminder/create").with(oauth2Login().attributes(attrs -> attrs.put("sub", "test-google-id"))).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Test Reminder")).andExpect(jsonPath("$.notified").value(false));

    }

    @Test
    void testGetReminderList() throws Exception {
        Reminder reminder = new Reminder();
        reminder.setTitle("Test");
        reminder.setDescription("Desc");
        reminder.setRemind(LocalDateTime.now().plusHours(1));
        reminder.setNotified(false);
        reminder.setUser(testUser);
        reminderRepository.save(reminder);

        mockMvc.perform(get("/api/v1/reminder/list").with(oauth2Login().attributes(attrs -> attrs.put("sub", "test-google-id")))).andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value("Test"));
    }

    @Test
    void testDeleteReminder() throws Exception {
        Reminder reminder = new Reminder();
        reminder.setTitle("To Delete");
        reminder.setDescription("Desc");
        reminder.setRemind(LocalDateTime.now().plusHours(1));
        reminder.setNotified(false);
        reminder.setUser(testUser);
        reminder = reminderRepository.save(reminder);

        mockMvc.perform(delete("/api/v1/reminder/delete/" + reminder.getId()).with(oauth2Login().attributes(attrs -> attrs.put("sub", "test-google-id")))).andExpect(status().isOk()).andExpect(content().string("Напоминание удалено"));
    }

}