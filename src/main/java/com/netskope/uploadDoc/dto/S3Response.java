package com.netskope.uploadDoc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class S3Response {
    private String actualFileName;
    private String fileName;
    private String url;
}
