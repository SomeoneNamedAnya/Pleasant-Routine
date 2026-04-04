package org.app.user.mapper;

import org.app.user.domain.User;
import org.app.user.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);
}
