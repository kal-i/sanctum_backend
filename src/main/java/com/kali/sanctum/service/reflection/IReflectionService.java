package com.kali.sanctum.service.reflection;

import com.kali.sanctum.dto.request.CreateReflectionEntryRequest;
import com.kali.sanctum.dto.response.ReflectionDto;
import com.kali.sanctum.model.Reflection;

public interface IReflectionService {
    Reflection getById(Long id);
    Reflection addEntry(CreateReflectionEntryRequest request);
    ReflectionDto convertToDto(Reflection reflection);
}
