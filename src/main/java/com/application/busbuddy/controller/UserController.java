package com.application.busbuddy.controller;

import com.application.busbuddy.dto.request.ProviderRequestDTO;
import com.application.busbuddy.dto.request.UserRequestDTO;
import com.application.busbuddy.dto.response.ProviderResponseDTO;
import com.application.busbuddy.dto.response.UserResponseDTO;
import com.application.busbuddy.model.enums.Role;
import com.application.busbuddy.service.ProviderService;
import com.application.busbuddy.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    private final ProviderService providerService;

    @PostMapping
    public ResponseEntity<?> createUserOrProvider(@Valid @RequestBody UserRequestDTO dto) {
        if (dto.getRole() == Role.PROVIDER) {
            ProviderRequestDTO providerDTO = ProviderRequestDTO.builder()
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .password(dto.getPassword())
                    .build();
            return ResponseEntity.ok(providerService.createProvider(providerDTO));
        } else {
            return ResponseEntity.ok(userService.createUser(dto));
        }
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/providers")
    public ResponseEntity<List<ProviderResponseDTO>> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserOrProviderById(@PathVariable Long id, @RequestParam Role role) {
        if (role == Role.PROVIDER) {
            return ResponseEntity.ok(providerService.getProviderById(id));
        } else {
            return ResponseEntity.ok(userService.getUserById(id));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserOrProvider(@PathVariable Long id, @Valid @RequestBody UserRequestDTO dto) {
        if (dto.getRole() == Role.PROVIDER) {
            ProviderRequestDTO providerDTO = ProviderRequestDTO.builder()
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .password(dto.getPassword())
                    .build();
            return ResponseEntity.ok(providerService.updateProvider(id, providerDTO));
        } else {
            return ResponseEntity.ok(userService.updateUser(id, dto));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserOrProvider(@PathVariable Long id, @RequestParam Role role) {
        if (role == Role.PROVIDER) {
            providerService.deleteProvider(id);
        } else {
            userService.deleteUser(id);
        }
        return ResponseEntity.noContent().build();
    }
}
