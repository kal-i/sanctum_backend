package com.kali.sanctum.service.user;

import com.kali.sanctum.dto.request.CreateUserRequest;
import com.kali.sanctum.dto.request.UpdateUserRequest;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getStandardUsers() {
        return userRepository.findByRole(Role.USER);
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User createUser(CreateUserRequest createUserRequest) {
        return Optional.of(createUserRequest)
                .filter(user -> !userRepository.existsByEmail(user.getEmail()))
                .map(req -> {
                    User user = User.builder()
                            .email(req.getEmail())
                            .password(passwordEncoder.encode(req.getPassword()))
                            .role(Role.USER)
                            .build();

                    User savedUser = userRepository.save(user);

                    auditLogService.logAction(
                            savedUser.getId(),
                            AuditLogType.CREATE_USER,
                            savedUser.getId(),
                            "Created User: " + user.getEmail()
                    );

                    return savedUser;
                }).orElseThrow(() -> new AlreadyExistsException(createUserRequest.getEmail() + " already exists"));
    }

    @Override
    public User updateUserProfile(Long id, UpdateUserRequest updateUserRequest) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(updateUserRequest.getUsername());

                    User updatedUser = userRepository.save(existingUser);

                    auditLogService.logAction(
                            updatedUser.getId(),
                            AuditLogType.UPDATE_USER,
                            updatedUser.getId(),
                            "Updated username to " + updatedUser.getUsername()
                    );

                    return updatedUser;
                }).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User updateUserRole(Long id, Role role) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setRole(role);

                    User updatedUser = userRepository.save(existingUser);
                    User actorUser = getAuthenticatedUser(); // get current user performing the task [Admin, SuperAdmin]

                    auditLogService.logAction(
                            actorUser.getId(),
                            AuditLogType.UPDATE_USER,
                            updatedUser.getId(),
                            "Updated the role of user " + updatedUser.getEmail() + " to " + updatedUser.getRole()
                    );

                    return updatedUser;
                }).orElseThrow(() -> new ResourceNotFoundException("User not found"));    }

    @Transactional
    @Override
    public void grantPermission(Long id, String permissionName) {
        User user = getUserById(id);

        Permission permission = permissionRepository.findByName(permissionName)
                .orElseGet(() -> permissionRepository.save(
                        Permission.builder().name(permissionName).build()
                        ));

        if (user.getPermissions() == null) {
            user.setPermissions(new HashSet<>());
        }

        user.getPermissions().add(permission);

        User actorUser = getAuthenticatedUser();
        auditLogService.logAction(
                actorUser.getId(),
                AuditLogType.GRANT_PERMISSION,
                user.getId(),
                "Granted permission " + permission.getName() + " to user " + user.getEmail()
        );

        /*
        * Can be omitted because of @Transaction.
        * JPA will automatically persist changes at the end of the transaction.
        * */
        // userRepository.save(user);
    }

    @Transactional
    @Override
    public void revokePermission(Long userId, Long permissionId) {
        User user = getUserById(userId);
        Permission permission = permissionService.getPermissionById(permissionId);

        if (user.getPermissions() != null) {
            user.getPermissions().remove(permission);

            User actorUser = getAuthenticatedUser();
            auditLogService.logAction(
                    actorUser.getId(),
                    AuditLogType.GRANT_PERMISSION,
                    user.getId(),
                    "Revoked permission " + permission.getName() + " to user " + user.getEmail()
            );
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
                            "Deleted user with email: " + user.getEmail()
                    );
                }, () -> {
                    throw new ResourceNotFoundException("User not found");
                });
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
