package com.kali.sanctum.controller;

import com.kali.sanctum.dto.request.CreateUserRequest;
import com.kali.sanctum.dto.request.GrantPermissionRequest;
import com.kali.sanctum.dto.request.UpdateUserRequest;
import com.kali.sanctum.dto.request.UpdateUserRoleRequest;
import com.kali.sanctum.dto.request.UploadProfileRequest;
import com.kali.sanctum.dto.response.ApiResponse;
import com.kali.sanctum.dto.response.UserDto;
import com.kali.sanctum.enums.Role;
import com.kali.sanctum.exceptions.AlreadyExistsException;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final IUserService userService;

    /*
     * RBAC + PBAC
     * SUPER_ADMIN is authorized to view all users by default. (RBAC)
     * Other than them, requires a VIEW_ALL_USERS permission to be able to access
     * it. (PBAC)
     */
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('VIEW_ALL_USER')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<UserDto> users = userService.getAllUsers(page, size);
            return ResponseEntity.ok(new ApiResponse("Successfully retrieved all users", users));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/standard")
    public ResponseEntity<ApiResponse> getStandardUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<UserDto> standardUsers = userService.getStandardUsers(page, size);
            return ResponseEntity
                    .ok(new ApiResponse("Successfully retrieved standard or non-privileged users", standardUsers));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse> getUsersByRole(
            @PathVariable Role role,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<UserDto> users = userService.getUsersByRole(role, page, size);
            return ResponseEntity
                    .ok(new ApiResponse("Successfully retrieved users with " + role.name() + " role", users));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('VIEW_ALL_USER')")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
        try {
            UserDto user = userService.getUserById(userId);
            return ResponseEntity.ok(new ApiResponse("Successfully retrieved user", user));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser() {
        try {
            UserDto user = userService.getCurrentUser();
            return ResponseEntity.ok().body(new ApiResponse("Successfully retrieve current user", user));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        try {
            UserDto user = userService.createUser(createUserRequest);
            return ResponseEntity.ok(new ApiResponse("Successfully created a new user", user));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PatchMapping("/{userId}/profile")
    public ResponseEntity<ApiResponse> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        try {
            UserDto updatedUser = userService.updateUserProfile(userId, updateUserRequest);
            return ResponseEntity.ok(new ApiResponse("Successfully update user profile", updatedUser));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{userId}/role")
    public ResponseEntity<ApiResponse> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequest updateUserRoleRequest) {
        try {
            UserDto updatedUser = userService.updateUserRole(userId, updateUserRoleRequest);
            return ResponseEntity.ok(new ApiResponse("Successfully updated user role", updatedUser));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/{userId}/permissions")
    public ResponseEntity<ApiResponse> grantPermission(
            @PathVariable Long userId,
            @Valid @RequestBody GrantPermissionRequest grantPermissionRequest) {
        try {
            userService.grantPermission(userId, grantPermissionRequest);
            return ResponseEntity.ok(new ApiResponse("Successfully granted permissions", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{userId}/permissions/{permissionId}")
    public ResponseEntity<ApiResponse> revokePermission(
            @PathVariable Long userId,
            @PathVariable Long permissionId) {
        try {
            userService.revokePermission(userId, permissionId);
            return ResponseEntity.ok(new ApiResponse("Successfully revoked a permission", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('DELETE_USER')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponse("Successfully deleted user", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/upload-profile")
    public ResponseEntity<ApiResponse> uploadProfile(@Valid UploadProfileRequest uploadProfileRequest) {
        try {
            String filename = userService.uploadProfile(uploadProfileRequest);
            return ResponseEntity.ok(new ApiResponse("Successfully uploaded", filename));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to upload profile: " + e.getMessage(), null));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Resource> loadProfile() {
        try {
            Resource image = userService.loadProfile();

            // Determine the content type based on file extension (simple way)
            String contentType = Files.probeContentType(Paths.get(image.getFile().getAbsolutePath()));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                    .body(image);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
