package io.repsy.storage;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {
    void store(String path, InputStream data, long size, String contentType) throws Exception;
    byte[] load(String path) throws Exception;
}