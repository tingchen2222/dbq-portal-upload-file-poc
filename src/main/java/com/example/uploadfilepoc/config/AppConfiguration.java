package com.example.uploadfilepoc.config;

import com.example.uploadfilepoc.services.BucketSetupService;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class AppConfiguration {

    private BucketSetupService bucketSetupService;

    public AppConfiguration(BucketSetupService bucketSetupService) {
        this.bucketSetupService = bucketSetupService;
    }

    // config to create bucket at start up if it doesn't exist
    @PostConstruct
    public void setup() {
        bucketSetupService.createBucketIfNotExists();
    }
}
