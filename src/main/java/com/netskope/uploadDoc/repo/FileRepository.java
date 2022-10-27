package com.netskope.uploadDoc.repo;

import com.netskope.uploadDoc.model.FileMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileMap, Long> {
    public FileMap findByHashedFileName(String fileName);
}
