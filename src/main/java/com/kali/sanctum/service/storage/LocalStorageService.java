package com.kali.sanctum.service.storage;

import com.kali.sanctum.enums.AuditLogType;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.model.User;
import com.kali.sanctum.repository.UserRepository;
import com.kali.sanctum.service.audit.IAuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IAuditLogService auditLogService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String store(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        // Sets a unique file name
        String filename = "user-" + userId + "-" + System.currentTimeMillis() + "-" + file.getOriginalFilename();
        // Sets the file path, converting the uploadDir + filename into an absolute path
        Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filename);

        // Ensure dir exist, create if it doesn't
        Files.createDirectories(filePath.getParent());
        // Copies file data to local disk at the target path
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        user.setProfileImageUrl(filename);
        userRepository.save(user);

        auditLogService.logAction(
                user.getId(),
                AuditLogType.UPDATE_USER,
                user.getId(),
                "User profile uploaded " + filename
        );

        return filename;
    }

    @Override
    public Resource load(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            System.out.println("[DEBUG] Tryig to load file: " + filePath);

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
