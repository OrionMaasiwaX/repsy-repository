package io.repsy.storage.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileSystemStorageStrategy {

    private final Path basePath;

    public FileSystemStorageStrategy(String basePath) {
        this.basePath = Path.of(basePath);
    }

    public void write(String relativePath, InputStream data) throws IOException {
        Path target = basePath.resolve(relativePath);
        Files.createDirectories(target.getParent());
        Files.copy(data, target, StandardCopyOption.REPLACE_EXISTING);
    }

    public byte[] read(String relativePath) throws IOException {
        Path target = basePath.resolve(relativePath);
        return Files.readAllBytes(target);
    }
}