package com.kali.sanctum.service.user;

import com.kali.sanctum.dto.request.CreateUserRequest;
import com.kali.sanctum.dto.request.UpdateUserRequest;
import com.kali.sanctum.dto.response.UserDto;
import com.kali.sanctum.enums.Role;
import com.kali.sanctum.model.User;

import java.util.List;

public interface IUserService {
    List<User> getAllUsers();
    List<User> getStandardUsers();
    List<User> getUsersByRole(Role role);
    User getUserById(Long id);
    User createUser(CreateUserRequest createUserRequest);
    User updateUserProfile(Long id, UpdateUserRequest updateUserRequest);
    User updateUserRole(Long id, Role role);
    void grantPermission(Long id, String permissionName);
    void revokePermission(Long userId, Long permissionId);
    void deleteUser(Long id);
    UserDto convertToDto(User user);
    User getAuthenticatedUser();
}
