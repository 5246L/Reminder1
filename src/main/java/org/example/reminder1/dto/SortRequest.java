package org.example.reminder1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SortRequest {
    @NotBlank(message = "Обязательное поле")
    private String by;
    private String order = "desc";
}
