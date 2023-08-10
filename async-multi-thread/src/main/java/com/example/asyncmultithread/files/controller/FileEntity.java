package com.example.asyncmultithread.files.controller;


import com.example.asyncmultithread.files.FileStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "file")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private FileStatus status;

}
