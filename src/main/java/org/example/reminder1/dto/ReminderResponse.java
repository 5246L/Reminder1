package org.example.reminder1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReminderResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime remind;
    private Boolean notified;
}
