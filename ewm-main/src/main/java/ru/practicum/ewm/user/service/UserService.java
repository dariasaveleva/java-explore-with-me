package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.UserDto;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    List<UserDto> findUsers(List<Long> userIds, Pageable pageable);

    UserDto createUser(UserDto userDto);

    void delete(long userId);

}
