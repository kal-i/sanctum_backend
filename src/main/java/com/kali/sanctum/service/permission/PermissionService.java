package com.kali.sanctum.service.permission;

import com.kali.sanctum.exceptions.AlreadyExistsException;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.model.Permission;
import com.kali.sanctum.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PermissionService implements IPermissionService {
    private final PermissionRepository permissionRepository;


    @Override
    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission does not exist"));
    }

    @Override
    public Permission addPermission(String name) {
        return Optional.of(name)
                .filter(permission -> !permissionRepository.existsByName(name))
                .map(per -> {
                    Permission permission = Permission.builder()
                            .name(name)
                            .build();
                    return permissionRepository.save(permission);
                }).orElseThrow(() -> new AlreadyExistsException(name + " already exist"));
    }
}
