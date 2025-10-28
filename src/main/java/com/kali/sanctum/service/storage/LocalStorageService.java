package com.kali.sanctum.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class LocalStorageService implements IStorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String store(String filename, MultipartFile file) throws IOException {
        // Sets the file path, converting the uploadDir + filename into an absolute path
        Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filename);

        // Ensure dir exist, create if it doesn't
        Files.createDirectories(filePath.getParent());
        // Copies file data to local disk at the target path
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    @Override
    public Resource load(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            System.out.println("[DEBUG] Trying to load file: " + filePath);

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File does not exist or is not readable: " + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load file", e);
        }
    }
}
