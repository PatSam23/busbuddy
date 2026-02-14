package com.application.busbuddy.controller;

import com.application.busbuddy.config.JwtUtil;
import com.application.busbuddy.exception.ResourceNotFoundException;
import com.application.busbuddy.model.Bus;
import com.application.busbuddy.model.Provider;
import com.application.busbuddy.model.User;
import com.application.busbuddy.repository.BusRepository;
import com.application.busbuddy.repository.ProviderRepository;
import com.application.busbuddy.repository.UserRepository;
import com.application.busbuddy.service.impl.ImageServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final ImageServiceImpl imageService;
    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;
    private final BusRepository busRepository;

    /**
     * Upload user profile photo
     */
    @PostMapping("/users/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> uploadUserProfilePhoto(@RequestParam("file") MultipartFile file) {
        try {
            String username = JwtUtil.getLoggedInUsername();
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Delete old profile photo if exists
            if (user.getProfilePhotoUrl() != null && !user.getProfilePhotoUrl().isEmpty()) {
                imageService.deleteImage(user.getProfilePhotoUrl());
            }

            // Upload new photo
            String imageUrl = imageService.uploadUserProfilePhoto(file, user.getId());

            // Update user entity
            user.setProfilePhotoUrl(imageUrl);
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile photo uploaded successfully");
            response.put("imageUrl", imageUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error uploading user profile photo", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Upload provider gallery photo
     */
    @PostMapping("/providers/gallery")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<Map<String, Object>> uploadProviderPhoto(@RequestParam("file") MultipartFile file,
                                                                   @RequestParam(value = "index", required = false) Integer index) {
        try {
            String username = JwtUtil.getLoggedInUsername();
            Provider provider = providerRepository.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

            // Get current photos
            List<String> currentPhotos = new ArrayList<>(provider.getGalleryPhotoList());

            // If index is provided, replace that photo; otherwise add new photo
            int photoIndex = (index != null && index > 0) ? index : (currentPhotos.size() + 1);

            // Limit to reasonable number of photos (e.g., 10)
            if (photoIndex > 10) {
                return ResponseEntity.badRequest().body(Map.of("error", "Maximum 10 photos allowed"));
            }

            // Upload new photo
            String imageUrl = imageService.uploadProviderPhoto(file, provider.getId(), photoIndex);

            // Update the photos list
            // Ensure the list is large enough
            while (currentPhotos.size() < photoIndex) {
                currentPhotos.add("");
            }

            // Delete old photo if replacing
            if (photoIndex <= currentPhotos.size() && !currentPhotos.get(photoIndex - 1).isEmpty()) {
                imageService.deleteImage(currentPhotos.get(photoIndex - 1));
            }

            currentPhotos.set(photoIndex - 1, imageUrl);

            // Remove empty trailing elements
            while (!currentPhotos.isEmpty() && currentPhotos.get(currentPhotos.size() - 1).isEmpty()) {
                currentPhotos.remove(currentPhotos.size() - 1);
            }

            // Update provider entity
            provider.setGalleryPhotoList(currentPhotos);
            providerRepository.save(provider);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Provider photo uploaded successfully");
            response.put("imageUrl", imageUrl);
            response.put("photoIndex", photoIndex);
            response.put("totalPhotos", currentPhotos.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error uploading provider photo", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Upload bus photo (1-3 photos per bus)
     */
    @PostMapping("/buses/{busId}/photos")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<Map<String, Object>> uploadBusPhoto(@PathVariable Long busId,
                                                              @RequestParam("file") MultipartFile file,
                                                              @RequestParam("photoIndex") int photoIndex) {
        try {
            String username = JwtUtil.getLoggedInUsername();
            Provider provider = providerRepository.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));

            // Verify ownership
            if (!bus.getProvider().getId().equals(provider.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Unauthorized access to bus"));
            }

            if (photoIndex < 1 || photoIndex > 3) {
                return ResponseEntity.badRequest().body(Map.of("error", "Photo index must be 1, 2, or 3"));
            }

            // Delete old photo if exists
            String oldPhotoUrl = bus.getPhotoByIndex(photoIndex);
            if (oldPhotoUrl != null && !oldPhotoUrl.isEmpty()) {
                imageService.deleteImage(oldPhotoUrl);
            }

            // Upload new photo
            String imageUrl = imageService.uploadBusPhoto(file, busId, photoIndex);

            // Update bus entity
            bus.setPhotoByIndex(photoIndex, imageUrl);
            busRepository.save(bus);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Bus photo uploaded successfully");
            response.put("imageUrl", imageUrl);
            response.put("photoIndex", photoIndex);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error uploading bus photo", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all provider gallery photos
     */
    @GetMapping("/providers/gallery")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<Map<String, Object>> getProviderPhotos() {
        try {
            String username = JwtUtil.getLoggedInUsername();
            Provider provider = providerRepository.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

            List<String> photos = provider.getGalleryPhotoList();

            Map<String, Object> response = new HashMap<>();
            response.put("photos", photos);
            response.put("totalPhotos", photos.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching provider photos", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all bus photos
     */
    @GetMapping("/buses/{busId}/photos")
    public ResponseEntity<Map<String, Object>> getBusPhotos(@PathVariable Long busId) {
        try {
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));

            List<String> photos = bus.getAllPhotos();

            Map<String, Object> response = new HashMap<>();
            response.put("photos", photos);
            response.put("busId", busId);
            response.put("busNumber", bus.getBusNumber());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching bus photos", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete provider gallery photo
     */
    @DeleteMapping("/providers/gallery/{photoIndex}")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<Map<String, Object>> deleteProviderPhoto(@PathVariable int photoIndex) {
        try {
            String username = JwtUtil.getLoggedInUsername();
            Provider provider = providerRepository.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

            List<String> currentPhotos = new ArrayList<>(provider.getGalleryPhotoList());

            if (photoIndex < 1 || photoIndex > currentPhotos.size()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid photo index"));
            }

            // Delete from Cloudinary
            String photoToDelete = currentPhotos.get(photoIndex - 1);
            imageService.deleteImage(photoToDelete);

            // Remove from list
            currentPhotos.remove(photoIndex - 1);

            // Update provider entity
            provider.setGalleryPhotoList(currentPhotos);
            providerRepository.save(provider);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Photo deleted successfully");
            response.put("remainingPhotos", currentPhotos.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting provider photo", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete bus photo
     */
    @DeleteMapping("/buses/{busId}/photos/{photoIndex}")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<Map<String, Object>> deleteBusPhoto(@PathVariable Long busId,
                                                              @PathVariable int photoIndex) {
        try {
            String username = JwtUtil.getLoggedInUsername();
            Provider provider = providerRepository.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));

            // Verify ownership
            if (!bus.getProvider().getId().equals(provider.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Unauthorized access to bus"));
            }

            if (photoIndex < 1 || photoIndex > 3) {
                return ResponseEntity.badRequest().body(Map.of("error", "Photo index must be 1, 2, or 3"));
            }

            // Delete from Cloudinary
            String photoToDelete = bus.getPhotoByIndex(photoIndex);
            if (photoToDelete != null && !photoToDelete.isEmpty()) {
                imageService.deleteImage(photoToDelete);

                // Update bus entity
                bus.setPhotoByIndex(photoIndex, null);
                busRepository.save(bus);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Bus photo deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting bus photo", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}