package com.kali.sanctum.data;

import com.kali.sanctum.enums.Role;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.model.Permission;
import com.kali.sanctum.model.User;
import com.kali.sanctum.repository.PermissionRepository;
import com.kali.sanctum.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Transactional
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<String> defaultPermissions = Set.of("VIEW_ALL_USER");
        createDefaultPermissionsIfNotExist(defaultPermissions);
        createdDefaultSuperAdminIfNotExist();
        createDefaultAdminWithPrivilegesIfNotExist();
    }

    private void createdDefaultSuperAdminIfNotExist() {
        User superAdmin = User.builder()
                .username("kali")
                .email("super_admin@email.com")
                .password(passwordEncoder.encode("12345678"))
                .role(Role.SUPER_ADMIN)
                .build();

        userRepository.save(superAdmin);
    }

    private void createDefaultAdminWithPrivilegesIfNotExist() {
        Permission adminDefaultPermissions = permissionRepository.findByName("VIEW_ALL_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Permission nto found"));

        User admin = User.builder()
                .username("kaii.lii")
                .email("admin@email.com")
                .password(passwordEncoder.encode("12345678"))
                .role(Role.ADMIN)
                .permissions(Set.of(adminDefaultPermissions))
                .build();

        userRepository.save(admin);
    }

    private void createDefaultPermissionsIfNotExist(Set<String> permissions) {
        permissions.stream()
                .filter(permission -> permissionRepository.findByName(permission).isEmpty())
                .map(Permission :: new).forEach(permissionRepository :: save);

    }
}
