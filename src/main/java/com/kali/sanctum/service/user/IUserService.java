package com.kali.sanctum.service.user;

import com.kali.sanctum.dto.request.CreateUserRequest;
import com.kali.sanctum.dto.request.GrantPermissionRequest;
import com.kali.sanctum.dto.request.UpdateUserRequest;
import com.kali.sanctum.dto.request.UpdateUserRoleRequest;
import com.kali.sanctum.dto.request.UploadProfileRequest;
import com.kali.sanctum.dto.response.UserDto;
import com.kali.sanctum.enums.Role;
import com.kali.sanctum.model.User;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;

public interface IUserService {
    Page<UserDto> getAllUsers(int page, int size);
    Page<UserDto> getStandardUsers(int page, int size);
    Page<UserDto> getUsersByRole(Role role, int page, int size);
    UserDto getCurrentUser();
    UserDto getUserById(Long id);
    User getUserEntityById(Long id);
    UserDto createUser(CreateUserRequest createUserRequest);
    UserDto updateUserProfile(Long id, UpdateUserRequest updateUserRequest);
    UserDto updateUserRole(Long id, UpdateUserRoleRequest updateUserRoleRequest);
    void grantPermission(Long id, GrantPermissionRequest grantPermissionRequest);
    void revokePermission(Long userId, Long permissionId);
    void deleteUser(Long id);
    String uploadProfile(UploadProfileRequest uploadProfileRequest);
    Resource loadProfile();
    List<UserDto> convertToDtos(List<User> users);
    UserDto convertToDto(User user);
    User getAuthenticatedUser();
}
