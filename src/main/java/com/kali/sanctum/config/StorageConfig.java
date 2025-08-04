package com.kali.sanctum.config;

import com.kali.sanctum.service.storage.IStorageService;
import com.kali.sanctum.service.storage.LocalStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @ConditionalOnProperty(name = "storage.type", havingValue = "local")
    public IStorageService localStorageService() {
        return new LocalStorageService();
    }

    // TODO: S3 bucket implementation
    // @ConditionalOnProperty(name = "storage.type", havingValue = "s3")
    // public IStorageService s3StorageService() {
    // return new S3StorageService();
    // }
}
