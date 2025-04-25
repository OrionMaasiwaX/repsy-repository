package io.repsy.controller;

import io.repsy.model.PackageMetadata;
import io.repsy.repository.PackageMetadataRepository;
import io.repsy.storage.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/")
public class PackageController {

    private final StorageService storageService;
    private final PackageMetadataRepository repository;

    public PackageController(StorageService storageService, PackageMetadataRepository repository) {
        this.storageService = storageService;
        this.repository = repository;
    }

    @PostMapping("{packageName}/{version}")
    public ResponseEntity<?> upload(
            @PathVariable String packageName,
            @PathVariable String version,
            @RequestPart("package.rep") MultipartFile packageFile,
            @RequestPart("meta.json") MultipartFile metaFile
    ) {
        try {
            if (!packageFile.getOriginalFilename().endsWith(".rep") ||
                    !metaFile.getOriginalFilename().equals("meta.json")) {
                return ResponseEntity.badRequest().body("Invalid file names or types.");
            }

            String pathPrefix = packageName + "/" + version + "/";

            storageService.store(pathPrefix + "package.rep", packageFile.getInputStream(),
                    packageFile.getSize(), packageFile.getContentType());
            storageService.store(pathPrefix + "meta.json", metaFile.getInputStream(),
                    metaFile.getSize(), metaFile.getContentType());

            String rawJson = new String(metaFile.getBytes(), StandardCharsets.UTF_8);

            PackageMetadata metadata = PackageMetadata.builder()
                    .name(packageName)
                    .version(version)
                    .author(extractAuthor(rawJson))
                    .rawJson(rawJson)
                    .build();

            repository.save(metadata);

            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("{packageName}/{version}/{fileName}")
    public ResponseEntity<byte[]> download(
            @PathVariable String packageName,
            @PathVariable String version,
            @PathVariable String fileName
    ) {
        try {
            String path = packageName + "/" + version + "/" + fileName;
            byte[] data = storageService.load(path);

            String contentType;
            if (fileName.endsWith(".json")) {
                contentType = "application/json";
            } else if (fileName.endsWith(".rep") || fileName.endsWith(".zip")) {
                contentType = "application/zip";
            } else {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .body(data);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    private String extractAuthor(String rawJson) {
        try {
            int i = rawJson.indexOf("\"author\"");
            if (i == -1) return "unknown";
            int q1 = rawJson.indexOf("\"", i + 8);
            int q2 = rawJson.indexOf("\"", q1 + 1);
            return rawJson.substring(q1 + 1, q2);
        } catch (Exception e) {
            return "unknown";
        }
    }
}