package io.repsy.storage;

import io.repsy.storage.filesystem.FileSystemStorageStrategy;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;

public class FileSystemStorageAdapter implements StorageService {

    private final FileSystemStorageStrategy strategy;

    public FileSystemStorageAdapter(String basePath) {
        this.strategy = new FileSystemStorageStrategy(basePath);
    }

    @Override
    public void store(String path, InputStream data, long size, String contentType) throws Exception {
        strategy.write(path, data);
    }

    @Override
    public byte[] load(String path) throws Exception {
        return strategy.read(path);
    }
}