package ru.practicum.ewm.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewUserRequest {
    @Email
    @NotBlank
    @Size(min = 6, max = 254, message = "Длина email не может быть меньше 6 и больше 254 символов")
    private String email;
    @NotBlank
    @Size(min = 2, max = 250, message = "Длина имени не может быть меньше 2 и больше 250 символов")
    private String name;
}
