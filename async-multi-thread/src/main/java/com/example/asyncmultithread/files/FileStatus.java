package com.example.asyncmultithread.files;

public enum FileStatus {

    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    FAIL("FAIL");

    private String status;

    FileStatus(String status) {
        this.status = status;
    }
}
