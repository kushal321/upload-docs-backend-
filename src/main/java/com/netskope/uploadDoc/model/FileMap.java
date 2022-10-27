package com.netskope.uploadDoc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FileMap {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fileName;

    public FileMap(String fileName, String hashedFileName) {
        this.fileName = fileName;
        this.hashedFileName = hashedFileName;
    }

    private String hashedFileName;
}
