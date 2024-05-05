package ru.practicum.ewm.user.dto.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

@UtilityClass
@Slf4j
public class UserMapper {
    public User fromUserCreationDto(NewUserRequest newUserRequest) {
        log.info("UserMapper: конвертация NewUserRequest в User");
        User user = new User();
        user.setEmail(newUserRequest.getEmail());
        user.setName(newUserRequest.getName());
        return user;
    }

    public UserDto toUserDto(User user) {
        log.info("UserMapper: конвертация User в UserDto");
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        return userDto;
    }

    public UserShortDto toUserShortDto(User user) {
        log.info("UserMapper: конвертация User в UserShortDto");
        UserShortDto shortDto = new UserShortDto();
        shortDto.setId(user.getId());
        shortDto.setName(user.getName());
        return shortDto;
    }
}
