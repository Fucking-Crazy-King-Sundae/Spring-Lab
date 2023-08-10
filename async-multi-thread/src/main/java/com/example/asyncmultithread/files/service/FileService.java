package com.example.asyncmultithread.files.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.example.asyncmultithread.files.FileStatus;
import com.example.asyncmultithread.files.controller.FileEntity;
import com.example.asyncmultithread.files.controller.repository.FileRepository;
import com.example.asyncmultithread.files.controller.support.exception.UncaughtException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private final AmazonS3 s3Client;
    private final FileRepository fileRepository;
    private final String BUCKET = "file-bucket";
    int count = 0;


    public FileService(AmazonS3 s3Client, FileRepository fileRepository) {
        this.s3Client = s3Client;
        this.fileRepository = fileRepository;
    }

    public byte[] download(final String fileKey) {
        S3Object fullObject = s3Client.getObject(BUCKET, fileKey);

        if (fullObject == null) {
            throw new UncaughtException("File Does Not Exist");
        }

        try {
            return IOUtils.toByteArray(fullObject.getObjectContent());
        } catch (IOException e) {
            throw new UncaughtException(e);
        }
    }


    public String upload(final MultipartFile file) {
        String threadName = Thread.currentThread().getName(); // 현재 스레드의 이름(ID) 가져오기
        logger.info("upload started in thread: {}", threadName);

        if (file.isEmpty()) {
            throw new UncaughtException("File is Empty");
        }

        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            PutObjectRequest request = new PutObjectRequest(BUCKET, key, file.getInputStream(), metadata);
            PutObjectResult result = s3Client.putObject(request);
            return key;
        } catch (IOException e) {
            throw new UncaughtException(e);
        }
    }

    public List<String> multiUpload(final List<MultipartFile> files) {
        return files.stream()
                .map(this::upload)
                .collect(Collectors.toList());
    }

    @Async("asyncTaskExecutor")
    @Transactional
    public void voidUpload(final String name, final MultipartFile file) {
        String threadName = Thread.currentThread().getName(); // 현재 스레드의 이름(ID) 가져오기
        logger.info("voidUpload started in thread: {}", threadName);

        if (file.isEmpty()) {
            throw new UncaughtException("File is Empty");
        }

        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            PutObjectRequest request = new PutObjectRequest(BUCKET, key, file.getInputStream(), metadata);
            PutObjectResult result = s3Client.putObject(request);
        } catch (IOException e) {
            throw new UncaughtException(e);
        }

        FileEntity fileEntity = this.fileRepository.findByName(name);
        fileEntity.setStatus(FileStatus.SUCCESS);

        System.out.println("success finish :" + threadName);

    }

    @Transactional
    public Map<String, MultipartFile> saveFiles(List<MultipartFile> files) throws Exception {
        Map<String, MultipartFile> map = new HashMap<>();

        for (MultipartFile file : files) {
            map.put(this.generateFileName(file), file);
        }

        return map;
    }

    public String generateFileName(MultipartFile file) {
        String name = "ONE_" + file.getOriginalFilename();

        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(name);
        fileEntity.setStatus(FileStatus.PENDING);

        this.fileRepository.save(fileEntity);

        return name;
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<String> asyncUpload(final MultipartFile file) {
        return CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            logger.info("asyncUpload started in thread: {}", threadName);

            if (file.isEmpty()) {
                throw new UncaughtException("File is Empty");
            }

            String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
            System.out.println("key : " + key);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            try {
                PutObjectRequest request = new PutObjectRequest(BUCKET, key, file.getInputStream(), metadata);
                PutObjectResult result = s3Client.putObject(request);
            } catch (IOException e) {
                throw new UncaughtException(e);
            }

            return key; // Return the key generated for this file
        });
    }


}
