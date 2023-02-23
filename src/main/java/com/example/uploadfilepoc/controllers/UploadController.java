package com.example.uploadfilepoc.controllers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/demo")
@CrossOrigin(originPatterns = {"http://*:*"})
public class UploadController {
    private AmazonS3 amazonS3;
    private String bucketName = "upload-poc";

    public UploadController(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @PostMapping("/upload")
    public void uploadFiles(@RequestParam("files") List<MultipartFile> files) throws IOException {
        ObjectMetadata metaData = new ObjectMetadata();
        for (MultipartFile file : files) {
            PutObjectRequest request = null;
            metaData.setContentType(file.getContentType());
            metaData.setContentLength(file.getSize());
            request = new PutObjectRequest(bucketName, file.getOriginalFilename(), file.getInputStream(), metaData);
            amazonS3.putObject(request);
        }
    }

    @GetMapping("/download/{fileKey}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileKey ) {
        S3Object s3Object = amazonS3.getObject(bucketName, fileKey);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        byte[] fileContents;

        try {
            fileContents = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file contents from s3", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(fileContents.length);
        headers.set("Content-Disposition", "attachment; filename=\"" + fileKey + "\"");
        return new ResponseEntity<>(fileContents, headers, HttpStatus.OK);
    }

    @GetMapping("/allFiles")
    public List<String> allFiles() {
        List<String> fileNames = new ArrayList<>();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
        ObjectListing objects = amazonS3.listObjects(listObjectsRequest);

        // usually capped at 1k files
        do {
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                fileNames.add(objectSummary.getKey());
            }
            objects = amazonS3.listNextBatchOfObjects(objects);
        } while (objects.isTruncated());

        return fileNames;
    }

    @DeleteMapping("/delete/{fileKey}")
    public void deleteFileByKey(@PathVariable String fileKey) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileKey));
    }
}

