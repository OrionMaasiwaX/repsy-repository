package io.repsy.storage;

import io.repsy.storage.object.ObjectStorageStrategy;

import java.io.InputStream;

public class ObjectStorageAdapter implements StorageService {

    private final ObjectStorageStrategy strategy;

    public ObjectStorageAdapter(String endpoint, String accessKey, String secretKey, String bucket) {
        this.strategy = new ObjectStorageStrategy(endpoint, accessKey, secretKey, bucket);
    }

    @Override
    public void store(String path, InputStream data, long size, String contentType) throws Exception {
        strategy.write(path, data, size, contentType);
    }

    @Override
    public byte[] load(String path) throws Exception {
        return strategy.read(path).readAllBytes();
    }
}