package com.application.busbuddy.mapper;

import com.application.busbuddy.dto.request.UserRequestDTO;
import com.application.busbuddy.dto.response.UserResponseDTO;
import com.application.busbuddy.model.User;

public class UserMapper {

    public static User toEntity(UserRequestDTO dto) {
        if (dto == null) return null;
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .Role(dto.getRole())
                .build();
    }

    public static UserResponseDTO toDTO(User user) {
        if (user == null) return null;
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
