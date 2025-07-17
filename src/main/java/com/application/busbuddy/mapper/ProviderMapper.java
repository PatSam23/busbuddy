package com.application.busbuddy.mapper;

import com.application.busbuddy.dto.ProviderDTO;
import com.application.busbuddy.model.Provider;

public class ProviderMapper {

    public static ProviderDTO toDTO(Provider provider) {
        if (provider == null) return null;
        return ProviderDTO.builder()
                .id(provider.getId())
                .name(provider.getName())
                .email(provider.getEmail())
                .build();
    }

    public static Provider toEntity(ProviderDTO dto) {
        if (dto == null) return null;
        return Provider.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }
}
