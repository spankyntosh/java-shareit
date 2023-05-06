package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto()
                .toBuilder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User dtoToModel(UserDto userDto, Integer userId) {
        return new User()
                .toBuilder()
                .id(userId)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static Collection<UserDto> toUserDtos(Collection<User> users) {
        return users.stream().map(user -> toUserDto(user)).collect(toList());
    }

}
