package com.example.uploadfilepoc.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import org.springframework.stereotype.Service;

@Service
public class BucketSetupService {

    private AmazonS3 s3Client;
    private String bucketName = "upload-poc";

    public BucketSetupService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }
    public void createBucketIfNotExists() {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            s3Client.createBucket(new CreateBucketRequest(bucketName));
        }
    }
}
