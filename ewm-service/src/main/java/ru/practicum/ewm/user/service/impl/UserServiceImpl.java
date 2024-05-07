package ru.practicum.ewm.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.exceptions.NotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.JpaUserRepository;
import ru.practicum.ewm.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final JpaUserRepository userRepository;

    @Transactional
    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        log.info("UserService: добавление пользователя {}", newUserRequest.toString());
        return UserMapper.toUserDto(userRepository.save(UserMapper.fromUserCreationDto(newUserRequest)));
    }

    @Override
    public List<UserDto> findUsers(List<Long> ids, int from, int size) {
        log.info("UserService: получение пользователей с id {} с номера {}", ids, from);
        int page = from / size;
        Sort sort = Sort.by(ASC, "id");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<User> users;

        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageRequest);
        } else {
            users = userRepository.findByIdIn(ids, pageRequest);
        }

        if (!users.hasContent()) {
            return new ArrayList<>();
        } else {
            return users.getContent().stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("UserService: удаление пользователя с id {}", id);
        isRegisteredUser(id);
        userRepository.deleteById(id);
    }

    private void isRegisteredUser(Long id) {
        log.info("UserService: проверка регистрации пользователя");
        userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format(
                "User with id=%d was not found", id)));
    }
}
