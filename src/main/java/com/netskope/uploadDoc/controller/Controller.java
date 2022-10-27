package com.netskope.uploadDoc.controller;

import com.netskope.uploadDoc.dto.S3Response;
import com.netskope.uploadDoc.service.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

    @Autowired ServiceImpl service;

    @GetMapping("/get-all-docs")
    public List<S3Response> getAllDocs() {
        return service.getAllDocs();
    }

    @PostMapping(value = "/upload-doc")
    public S3Response uploadDoc(@RequestParam String fileName, @RequestPart MultipartFile file) {
        return service.uploadDoc(fileName, file);
    }
}
