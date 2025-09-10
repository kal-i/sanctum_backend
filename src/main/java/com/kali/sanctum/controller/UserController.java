package com.kali.sanctum.controller;

import com.kali.sanctum.dto.request.CreateUserRequest;
import com.kali.sanctum.dto.request.UpdateUserRequest;
import com.kali.sanctum.dto.response.ApiResponse;
import com.kali.sanctum.enums.Role;
import com.kali.sanctum.exceptions.AlreadyExistsException;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.model.User;
import com.kali.sanctum.service.storage.IStorageService;
import com.kali.sanctum.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final IUserService userService;
    private final IStorageService storageService;

    /*
    * RBAC + PBAC
    * SUPER_ADMIN is authorized to view all users by default. (RBAC)
    * Other than them, requires a VIEW_ALL_USERS permission to be able to access it. (PBAC)
    * */
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('VIEW_ALL_USER')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(new ApiResponse("Retrieved all users", users));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/standard")
    public ResponseEntity<ApiResponse> getStandardUsers() {
        try {
            List<User> users = userService.getStandardUsers();
            return ResponseEntity.ok(new ApiResponse("Retrieved standard or non-privileged users", users));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/role")
    public ResponseEntity<ApiResponse> getUsersByRole(@RequestParam Role role) {
        try {
            List<User> users = userService.getUsersByRole(role);
            return ResponseEntity.ok(new ApiResponse("Retrieved users with " + role.name() + " role", users));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(new ApiResponse("User found", user));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        try {
            User user = userService.createUser(createUserRequest);
            return ResponseEntity.ok(new ApiResponse("User created", user));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PatchMapping("/{userId}/profile")
    public ResponseEntity<ApiResponse> updateUserProfile(@PathVariable Long userId, @RequestBody UpdateUserRequest updateUserRequest) {
        try {
            User updatedUser = userService.updateUserProfile(userId, updateUserRequest);
            return ResponseEntity.ok(new ApiResponse("User information updated", updatedUser));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{userId}/role")
    public ResponseEntity<ApiResponse> updateUserRole(@PathVariable Long userId, @RequestBody Role role) {
        try {
            User updatedUser = userService.updateUserRole(userId, role);
            return ResponseEntity.ok(new ApiResponse("User role updated", updatedUser));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/{userId}/permissions")
    public ResponseEntity<ApiResponse> grantPermission(@PathVariable Long userId, @RequestBody Set<String> permissionNames) {
        try {
            permissionNames.forEach(permissionName -> userService.grantPermission(userId, permissionName));
            return ResponseEntity.ok(new ApiResponse("Permission granted", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{userId}/permissions/{permissionId}")
    public ResponseEntity<ApiResponse> revokePermission(@PathVariable Long userId, @PathVariable Long permissionId) {
        try {
            userService.revokePermission(userId, permissionId);
            return ResponseEntity.ok(new ApiResponse("Permission revoked", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('DELETE_USER')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponse("User deleted", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/{userId}/upload-profile")
    public ResponseEntity<ApiResponse> uploadProfile(@PathVariable Long userId, @RequestParam("file")MultipartFile file) {
        try {
            String filename = storageService.store(file, userId);

            return ResponseEntity.ok(new ApiResponse("Profile uploaded", filename));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Profile upload failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{userId}/load-profile")
    public ResponseEntity<Resource> loadProfile(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);

            String imageUrl = user.getProfileImageUrl();
            if (imageUrl == null || imageUrl.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Resource image = storageService.load(imageUrl);

            // Determine the content type based on file extension (simple way)
            String contentType = Files.probeContentType(Paths.get(image.getFile().getAbsolutePath()));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(image);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
