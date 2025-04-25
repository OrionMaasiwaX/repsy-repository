package io.repsy.storage.object;

import io.minio.MinioClient;
import io.minio.GetObjectArgs;
import io.minio.PutObjectArgs;

import java.io.InputStream;

public class ObjectStorageStrategy {

    private final MinioClient minioClient;
    private final String bucket;

    public ObjectStorageStrategy(String endpoint, String accessKey, String secretKey, String bucket) {
        this.bucket = bucket;
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public void write(String objectName, InputStream data, long size, String contentType) throws Exception {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .stream(data, size, -1)
                .contentType(contentType)
                .build());
    }

    public InputStream read(String objectName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build());
    }
}