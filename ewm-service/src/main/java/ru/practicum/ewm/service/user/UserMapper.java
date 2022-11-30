package ru.practicum.ewm.service.user;

import ru.practicum.ewm.service.user.dto.NewUserDto;
import ru.practicum.ewm.service.user.dto.UserDto;

public class UserMapper {
    public static User toUser(NewUserDto userDto) {
        return new User(
                0L,
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
