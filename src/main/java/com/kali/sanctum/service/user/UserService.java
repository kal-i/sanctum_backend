package com.kali.sanctum.service.user;

import com.kali.sanctum.dto.request.CreateUserRequest;
import com.kali.sanctum.dto.request.GrantPermissionRequest;
import com.kali.sanctum.dto.request.UpdateUserRequest;
import com.kali.sanctum.dto.request.UpdateUserRoleRequest;
import com.kali.sanctum.dto.request.UploadProfileRequest;
import com.kali.sanctum.dto.response.UserDto;
import com.kali.sanctum.enums.AuditLogType;
import com.kali.sanctum.enums.Role;
import com.kali.sanctum.exceptions.AlreadyExistsException;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.model.Permission;
import com.kali.sanctum.model.User;
import com.kali.sanctum.repository.PermissionRepository;
import com.kali.sanctum.repository.UserRepository;
import com.kali.sanctum.service.audit.IAuditLogService;
import com.kali.sanctum.service.permission.IPermissionService;
import com.kali.sanctum.service.storage.IStorageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final IPermissionService permissionService;
    private final IAuditLogService auditLogService;
    private final IStorageService storageService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserDto> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> users = userRepository.findAll(pageable);
        return convertToDtoPage(users);
    }

    @Override
    public Page<UserDto> getStandardUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> standardUsers = userRepository.findByRole(Role.USER, pageable);
        return convertToDtoPage(standardUsers);
    }

    @Override
    public Page<UserDto> getUsersByRole(Role role, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> users = userRepository.findByRole(role, pageable);
        return convertToDtoPage(users);
    }

    private Page<UserDto> convertToDtoPage(Page<User> users) {
        return users.map(this::convertToDto);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = getUserEntityById(id);
        return convertToDto(user);
    }

    @Override
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    @Override
    public UserDto getCurrentUser() {
        User user = getAuthenticatedUser();
        return convertToDto(user);
    }

    @Override
    public UserDto createUser(CreateUserRequest createUserRequest) {
        return Optional.of(createUserRequest)
                .filter(user -> !userRepository.existsByEmail(user.email()))
                .map(req -> {
                    User user = User.builder()
                            .username(req.username())
                            .email(req.email())
                            .password(passwordEncoder.encode(req.password()))
                            .role(Role.USER)
                            .build();

                    User savedUser = userRepository.save(user);

                    auditLogService.logAction(
                            savedUser.getId(),
                            AuditLogType.CREATE_USER,
                            savedUser.getId(),
                            "Created User: " + savedUser.getEmail());

                    return convertToDto(savedUser);
                }).orElseThrow(() -> {
                    auditLogService.logAction(
                            null,
                            AuditLogType.CREATE_USER_ATTEMPT,
                            null,
                            "Attempted to create user with existing email: " + createUserRequest.email());
                    return new AlreadyExistsException("An account with this email already exists.");

                });
    }

    @Override
    public UserDto updateUserProfile(Long id, UpdateUserRequest updateUserRequest) {
        // check first if the resource (in this case, user)
        // the prob with check first approach is this somehow exposes that this ID exist
        User user = getAuthenticatedUser();

        if (!user.getId().equals(id)) {
            throw new ResourceNotFoundException("User not found.");
        }

        // we can skip checking the repository because we already know that this id
        // exist
        user.setUsername(updateUserRequest.username());

        User updatedUser = userRepository.save(user);

        auditLogService.logAction(
                updatedUser.getId(),
                AuditLogType.UPDATE_USER,
                updatedUser.getId(),
                "Updated username to " + updatedUser.getUsername());
        return convertToDto(updatedUser);
    }

    @Override
    public UserDto updateUserRole(Long id, UpdateUserRoleRequest updateUserRoleRequest) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setRole(updateUserRoleRequest.role());

                    User updatedUser = userRepository.save(existingUser);
                    User actorUser = getAuthenticatedUser();

                    auditLogService.logAction(
                            actorUser.getId(),
                            AuditLogType.UPDATE_USER,
                            updatedUser.getId(),
                            "Updated the role of user " + updatedUser.getEmail() + " to " + updatedUser.getRole());

                    return convertToDto(updatedUser);
                }).orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    @Transactional
    @Override
    public void grantPermission(Long id, GrantPermissionRequest grantPermissionRequest) {
        User user = getUserEntityById(id);
        User actorUser = getAuthenticatedUser();

        if (user.getPermissions() == null) {
            user.setPermissions(new HashSet<>());
        }

        grantPermissionRequest.permissions().forEach(permissionName -> {
            Permission permission = permissionRepository.findByName(permissionName)
                    .orElseGet(() -> permissionRepository.save(
                            Permission.builder().name(permissionName).build()));

            user.getPermissions().add(permission);

            auditLogService.logAction(
                    actorUser.getId(),
                    AuditLogType.GRANT_PERMISSION,
                    user.getId(),
                    "Granted permission " + permission.getName() + " to user " + user.getEmail());
        });

        /*
         * Can be omitted because of @Transactional.
         * JPA will automatically persist changes at the end of the transaction.
         */
        // userRepository.save(user);
    }

    @Transactional
    @Override
    public void revokePermission(Long userId, Long permissionId) {
        User user = getUserEntityById(userId);
        Permission permission = permissionService.getPermissionById(permissionId);

        if (user.getPermissions() != null) {
            user.getPermissions().remove(permission);

            User actorUser = getAuthenticatedUser();
            auditLogService.logAction(
                    actorUser.getId(),
                    AuditLogType.GRANT_PERMISSION,
                    user.getId(),
                    "Revoked permission " + permission.getName() + " to user " + user.getEmail());
        }
        // userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id)
                .ifPresentOrElse(user -> {
                    userRepository.delete(user);

                    User actorUser = getAuthenticatedUser();
                    auditLogService.logAction(
                            actorUser.getId(),
                            AuditLogType.DELETE_USER,
                            user.getId(),
                            "Deleted user with email: " + user.getEmail());
                }, () -> {
                    throw new ResourceNotFoundException("User not found.");
                });
    }

    @Override
    public String uploadProfile(UploadProfileRequest uploadProfileRequest) throws IOException {
        User user = getAuthenticatedUser();

        String filename = "user-" + user.getId() + "-" + System.currentTimeMillis() + "-" + uploadProfileRequest.imageFile().getOriginalFilename();
        storageService.store(filename, uploadProfileRequest.imageFile());

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
    public Resource loadProfile() {
        User user = getAuthenticatedUser();

        String imageUrl = user.getProfileImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new ResourceNotFoundException("No profile uploaded.");
        }

        Resource image = storageService.load(imageUrl);
        return image;
    }

    @Override
    public List<UserDto> convertToDtos(List<User> users) {
        return users.stream()
                .map(this::convertToDto).toList();
    }

    @Override
    public UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }
}
