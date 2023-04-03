package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private Integer id;
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    private String name;
    @Email(message = "Неверно указан формат электронной почты")
    @NotBlank(message = "почта пользователя не должно быть пустой")
    private String email;
}
