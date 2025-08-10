package com.application.busbuddy.mapper;

import com.application.busbuddy.dto.request.ProviderRequestDTO;
import com.application.busbuddy.dto.response.ProviderResponseDTO;
import com.application.busbuddy.model.Provider;

public class ProviderMapper {

    public static ProviderResponseDTO toDTO(Provider provider) {
        if (provider == null) return null;
        return ProviderResponseDTO.builder()
                .id(provider.getId())
                .name(provider.getName())
                .email(provider.getEmail())
                .role(provider.getRole())
                .build();
    }

    public static Provider toEntity(ProviderRequestDTO dto) {
        if (dto == null) return null;
        return Provider.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(dto.getRole())
                .build();
    }
}
