package com.netskope.uploadDoc.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.netskope.uploadDoc.dto.S3Response;
import com.netskope.uploadDoc.repo.FileRepository;
import com.netskope.uploadDoc.model.FileMap;
import lombok.SneakyThrows;
import org.joda.time.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ServiceImpl {

    @Autowired FileRepository repository;

    @Autowired private AmazonS3 amazonS3Client;

    @SneakyThrows
    public S3Response uploadDoc(String fileName, MultipartFile multipartFile) {

        if(!validate(multipartFile)) {
            throw new Exception("Validation Failed") ;
        }
        ObjectMetadata data = new ObjectMetadata();
        data.setContentType(multipartFile.getContentType());
        data.setContentLength(multipartFile.getSize());
        // masking values
        BasicAWSCredentials creds =
                new BasicAWSCredentials("***", "***");
        AmazonS3 s3client =
                AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).withCredentials(new AWSStaticCredentialsProvider(creds)).build();
        String hashedFileName = fileName + generateHash();
        s3client.putObject("netcracker-round", hashedFileName, multipartFile.getInputStream(), data);
        String url = s3client.getUrl("netcracker-round", hashedFileName).toExternalForm();
        repository.save(new FileMap(fileName, hashedFileName));
        S3Response response = new S3Response(fileName, hashedFileName, url);

        return response;
    }

    @SneakyThrows
    public String generateHash() {
        try (InputStream is = new ByteArrayInputStream(
                ((Long) DateTimeUtils.currentTimeMillis()).toString().getBytes(StandardCharsets.UTF_8))) {
            String tempString =  org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
            return tempString.substring(0,8);
        }
    }

    public boolean validate(MultipartFile multipartFile) {
        if(multipartFile.getContentType().equalsIgnoreCase("application/pdf")) {
            return true;
        }
        return false;
    }

    public List<S3Response> getAllDocs() {
        ListObjectsRequest listObjectsRequest =
                new ListObjectsRequest()
                        .withBucketName("netcracker-round");
        List<S3Response> responses = new ArrayList<>();

        ObjectListing objects = amazonS3Client.listObjects(listObjectsRequest);

        while (true) {
            List<S3ObjectSummary> objectSummaries = objects.getObjectSummaries();
            if (objectSummaries.isEmpty()) {
                break;
            }

            for (S3ObjectSummary item : objectSummaries) {
                if (!item.getKey().endsWith("/")) {
                    String url =
                            amazonS3Client.getUrl(objects.getBucketName(), item.getKey()).toExternalForm();
                    FileMap fileMap = repository.findByHashedFileName(item.getKey());
                    responses.add(new S3Response(
                            Objects.isNull(fileMap)
                                    ? null :
                                    fileMap.getFileName(),
                            item.getKey(),
                            url));
                }
            }

            objects = amazonS3Client.listNextBatchOfObjects(objects);
        }

        return responses;
    }
}
