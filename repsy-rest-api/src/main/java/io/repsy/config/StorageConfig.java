package io.repsy.config;

import io.repsy.storage.FileSystemStorageAdapter;
import io.repsy.storage.ObjectStorageAdapter;
import io.repsy.storage.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Value("${storage.strategy}")
    private String strategy;

    @Value("${storage.file.base-path:./packages}")
    private String fileBasePath;

    @Value("${storage.object.endpoint}")
    private String objectEndpoint;

    @Value("${storage.object.access-key}")
    private String objectAccessKey;

    @Value("${storage.object.secret-key}")
    private String objectSecretKey;

    @Value("${storage.object.bucket}")
    private String objectBucket;

    @Bean
    public StorageService storageService() {
        return switch (strategy) {
            case "file-system" -> new FileSystemStorageAdapter(fileBasePath);
            case "object-storage" -> new ObjectStorageAdapter(objectEndpoint, objectAccessKey, objectSecretKey, objectBucket);
            default -> throw new IllegalArgumentException("Invalid storage.strategy value: " + strategy);
        };
    }
}