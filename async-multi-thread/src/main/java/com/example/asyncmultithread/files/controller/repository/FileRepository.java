package com.example.asyncmultithread.files.controller.repository;

import com.example.asyncmultithread.files.controller.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    FileEntity findByName(String name);

}
