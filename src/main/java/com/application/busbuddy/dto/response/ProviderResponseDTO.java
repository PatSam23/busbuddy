package com.application.busbuddy.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderResponseDTO {
    private Long id;
    private String name;
    private String email;
}
