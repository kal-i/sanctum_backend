package com.kali.sanctum.service.permission;

import com.kali.sanctum.model.Permission;

public interface IPermissionService {
    Permission getPermissionById(Long id);
    Permission addPermission(String name);
}
