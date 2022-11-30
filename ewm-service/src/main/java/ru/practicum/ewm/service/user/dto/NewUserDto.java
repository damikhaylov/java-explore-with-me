package ru.practicum.ewm.service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewUserDto {
    @NotBlank
    @Size(min = 1, max = 255)
    private String name;
    @NotNull
    @Email
    @Size(min = 3, max = 128)
    private String email;
}
