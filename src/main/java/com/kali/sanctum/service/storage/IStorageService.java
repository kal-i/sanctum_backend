package com.kali.sanctum.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IStorageService {
    String store(MultipartFile file) throws IOException;
    Resource load(String fileName);
}
