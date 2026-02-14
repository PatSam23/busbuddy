package com.application.busbuddy.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl {

    private final Cloudinary cloudinary;

    /**
     * Upload user profile photo
     */
    public String uploadUserProfilePhoto(MultipartFile file, Long userId) throws IOException {
        validateImageFile(file);

        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", "busbuddy/users/profiles",
                "public_id", "user_" + userId + "_profile",
                "overwrite", true,
                "transformation", ObjectUtils.asMap(
                        "width", 300,
                        "height", 300,
                        "crop", "fill",
                        "gravity", "face",
                        "quality", "auto",
                        "format", "jpg"
                )
        );

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Upload provider photos (multiple scrollable photos)
     */
    public String uploadProviderPhoto(MultipartFile file, Long providerId, int photoIndex) throws IOException {
        validateImageFile(file);

        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", "busbuddy/providers/gallery",
                "public_id", "provider_" + providerId + "_photo_" + photoIndex,
                "overwrite", true,
                "transformation", ObjectUtils.asMap(
                        "width", 800,
                        "height", 600,
                        "crop", "fill",
                        "quality", "auto",
                        "format", "jpg"
                )
        );

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Upload bus photos (3 photos per bus)
     */
    public String uploadBusPhoto(MultipartFile file, Long busId, int photoIndex) throws IOException {
        validateImageFile(file);

        if (photoIndex < 1 || photoIndex > 3) {
            throw new IllegalArgumentException("Bus can have maximum 3 photos (index 1-3)");
        }

        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", "busbuddy/buses/gallery",
                "public_id", "bus_" + busId + "_photo_" + photoIndex,
                "overwrite", true,
                "transformation", ObjectUtils.asMap(
                        "width", 1000,
                        "height", 600,
                        "crop", "fill",
                        "quality", "auto",
                        "format", "jpg"
                )
        );

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Delete image from Cloudinary
     */
    public void deleteImage(String imageUrl) {
        try {
            // Extract public_id from URL
            String publicId = extractPublicIdFromUrl(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Successfully deleted image: {}", publicId);
        } catch (Exception e) {
            log.error("Failed to delete image: {}", imageUrl, e);
        }
    }

    /**
     * Validate uploaded file
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Check file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size cannot exceed 5MB");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        // Allowed formats
        if (!contentType.equals("image/jpeg") &&
                !contentType.equals("image/jpg") &&
                !contentType.equals("image/png") &&
                !contentType.equals("image/webp")) {
            throw new IllegalArgumentException("Only JPG, JPEG, PNG, and WebP formats are allowed");
        }
    }

    /**
     * Extract public_id from Cloudinary URL for deletion
     */
    private String extractPublicIdFromUrl(String imageUrl) {
        // Extract public_id from URL like:
        // https://res.cloudinary.com/your-cloud/image/upload/v123456789/busbuddy/users/profiles/user_1_profile.jpg
        try {
            String[] parts = imageUrl.split("/");
            int uploadIndex = -1;
            for (int i = 0; i < parts.length; i++) {
                if ("upload".equals(parts[i])) {
                    uploadIndex = i;
                    break;
                }
            }

            if (uploadIndex == -1) {
                throw new IllegalArgumentException("Invalid Cloudinary URL");
            }

            // Skip version if present (starts with 'v' followed by digits)
            int startIndex = uploadIndex + 1;
            if (startIndex < parts.length && parts[startIndex].matches("^v\\d+$")) {
                startIndex++;
            }

            // Join remaining parts and remove file extension
            StringBuilder publicId = new StringBuilder();
            for (int i = startIndex; i < parts.length; i++) {
                if (i > startIndex) publicId.append("/");
                publicId.append(parts[i]);
            }

            // Remove file extension
            String result = publicId.toString();
            int lastDot = result.lastIndexOf(".");
            if (lastDot > 0) {
                result = result.substring(0, lastDot);
            }

            return result;
        } catch (Exception e) {
            log.error("Failed to extract public_id from URL: {}", imageUrl, e);
            return "";
        }
    }
}