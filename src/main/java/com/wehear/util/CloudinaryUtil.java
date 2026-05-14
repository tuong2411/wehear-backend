package com.wehear.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
public class CloudinaryUtil {

    private final Cloudinary cloudinary;

    public CloudinaryUtil(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Uploads a file to Cloudinary with the specified folder.
     * 
     * @param file The file to upload.
     * @param folder The folder path on Cloudinary.
     * @return The secure URL of the uploaded file.
     * @throws IOException If an error occurs during upload.
     */
    public String upload(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "auto",
                        "folder", folder
                ));

        return (String) uploadResult.get("secure_url");
    }

    /**
     * Uploads a file to Cloudinary based on the type and purpose.
     * 
     * @param file The file to upload.
     * @param type Purpose of the file (avatar, lesson, community, video, dictionary).
     * @return The secure URL of the uploaded file.
     * @throws IOException If an error occurs during upload.
     */
    public String uploadByType(MultipartFile file, String type) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String folder = getFolderByType(file, type);
        return upload(file, folder);
    }

    private String getFolderByType(MultipartFile file, String type) {
        String contentType = file.getContentType();
        boolean isVideo = contentType != null && contentType.startsWith("video");

        if (isVideo) {
            if ("dictionary".equalsIgnoreCase(type)) {
                return "wehear/dictionary";
            }
            return "video";
        } else {
            // It's an image (or other, but we assume image based on logic)
            switch (type.toLowerCase()) {
                case "avatar":
                    return "image/avatar";
                case "lesson":
                    return "image/lesson";
                case "community":
                    return "image/community";
                case "dictionary":
                    return "wehear/dictionary";
                default:
                    return "image/community";
            }
        }
    }
}
