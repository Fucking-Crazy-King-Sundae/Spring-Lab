package com.example.asyncmultithread.files.controller;

import com.example.asyncmultithread.aop.ExecutionTimeLog;
import com.example.asyncmultithread.files.controller.response.ApiResponse;
import com.example.asyncmultithread.files.controller.support.ApiResponseGenerator;
import com.example.asyncmultithread.files.service.FileService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/v1/files")
@RestController
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(final @RequestParam String fileName) {
        return ApiResponseGenerator.of(fileService.download(fileName), fileName);
    }

    @PostMapping("/upload")
    public ApiResponse<String> upload(@RequestParam("file") MultipartFile file) {
        return ApiResponseGenerator.of(fileService.upload(file));
    }

    @PostMapping("/multi-upload")
    public ApiResponse<List<String>> multiUpload(@RequestParam("file") List<MultipartFile> file) {
        return ApiResponseGenerator.of(fileService.multiUpload(file));
    }

    @PostMapping("/multi-async-upload")
    @ExecutionTimeLog
    public Set<String> asyncUpload(@RequestParam("file") List<MultipartFile> file) throws Exception {
        Map<String, MultipartFile> result = this.fileService.saveFiles(file);

        result.forEach((k, v) -> {
            this.fileService.voidUpload(k, v);
        });

        return result.keySet();
    }

    @PostMapping("/v2/multi-async-upload")
    @ExecutionTimeLog
    public List<String> asyncUpload2(@RequestParam("file") List<MultipartFile> files) {
        System.out.println('?');
        List<String> keys = new ArrayList<>();
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (MultipartFile file : files) {
            CompletableFuture<String> future = fileService.asyncUpload(file);
            futures.add(future);
        }

        // Wait for all CompletableFuture instances to complete
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();

        // Collect the results from CompletableFuture instances
        for (CompletableFuture<String> future : futures) {
            try {
                String key = future.get();
                keys.add(key);
            } catch (Exception e) {
                // Handle exceptions if needed
            }
        }

        return keys;
    }


}
