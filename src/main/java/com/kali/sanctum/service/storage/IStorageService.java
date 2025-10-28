package com.kali.sanctum.service.storage;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {
    String store(String fileName, MultipartFile file) throws IOException;
    Resource load(String fileName);
}
