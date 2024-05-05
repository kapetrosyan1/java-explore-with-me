package ru.practicum.ewm.user.dto.mapper;

import ru.practicum.ewm.user.dto.UserCreationDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

public class UserDtoMapper {
    public static User fromUserCreationDto(UserCreationDto creationDto) {
        User user = new User();
        user.setEmail(creationDto.getEmail());
        user.setName(creationDto.getName());
        return user;
    }

    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        return userDto;
    }
}
