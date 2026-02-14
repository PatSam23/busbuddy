package com.application.busbuddy.config;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public class ImageUtil {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_PROVIDER_PHOTOS = 10;
    private static final int MAX_BUS_PHOTOS = 3;

    /**
     * Validate image file for upload
     */
    public static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size cannot exceed 5MB");
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Only JPG, JPEG, PNG, and WebP formats are allowed");
        }

        // Check file name
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name is required");
        }
    }

    /**
     * Generate optimized file name for upload
     */
    public static String generateFileName(String prefix, Long entityId, String suffix) {
        return String.format("%s_%d_%s_%d", prefix, entityId, suffix, System.currentTimeMillis());
    }

    /**
     * Clean up photo list by removing empty entries
     */
    public static List<String> cleanPhotoList(List<String> photos) {
        return photos.stream()
                .filter(url -> url != null && !url.trim().isEmpty())
                .toList();
    }

    /**
     * Validate photo index for different entities
     */
    public static void validatePhotoIndex(String entityType, int index) {
        switch (entityType.toLowerCase()) {
            case "provider" -> {
                if (index < 1 || index > MAX_PROVIDER_PHOTOS) {
                    throw new IllegalArgumentException("Provider photo index must be between 1 and " + MAX_PROVIDER_PHOTOS);
                }
            }
            case "bus" -> {
                if (index < 1 || index > MAX_BUS_PHOTOS) {
                    throw new IllegalArgumentException("Bus photo index must be between 1 and " + MAX_BUS_PHOTOS);
                }
            }
            default -> throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }
    }

    /**
     * Check if URL is a Cloudinary URL
     */
    public static boolean isCloudinaryUrl(String url) {
        return url != null && url.contains("cloudinary.com");
    }

    /**
     * Get image transformation URL for different sizes
     */
    public static String getTransformedImageUrl(String originalUrl, String transformation) {
        if (!isCloudinaryUrl(originalUrl)) {
            return originalUrl;
        }

        // Insert transformation parameters into Cloudinary URL
        // Example: https://res.cloudinary.com/cloud/image/upload/v123/folder/image.jpg
        // Becomes: https://res.cloudinary.com/cloud/image/upload/w_300,h_300,c_fill/v123/folder/image.jpg

        String uploadPattern = "/upload/";
        int uploadIndex = originalUrl.indexOf(uploadPattern);
        if (uploadIndex == -1) {
            return originalUrl;
        }

        return originalUrl.substring(0, uploadIndex + uploadPattern.length()) +
                transformation + "/" +
                originalUrl.substring(uploadIndex + uploadPattern.length());
    }

    /**
     * Get thumbnail URL (300x300)
     */
    public static String getThumbnailUrl(String originalUrl) {
        return getTransformedImageUrl(originalUrl, "w_300,h_300,c_fill,q_auto");
    }

    /**
     * Get medium size URL (600x400)
     */
    public static String getMediumUrl(String originalUrl) {
        return getTransformedImageUrl(originalUrl, "w_600,h_400,c_fill,q_auto");
    }

    /**
     * Get large size URL (1200x800)
     */
    public static String getLargeUrl(String originalUrl) {
        return getTransformedImageUrl(originalUrl, "w_1200,h_800,c_fill,q_auto");
    }
}