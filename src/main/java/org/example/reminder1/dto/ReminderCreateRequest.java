package org.example.reminder1.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReminderCreateRequest {

    @NotBlank (message = "Название обязательно")
    private String title;
    private String description;

    @NotNull(message = "Дата напоминания обязательна")
    @Future (message = "Дата должна быть в будущем")
    private LocalDateTime remind;
}
