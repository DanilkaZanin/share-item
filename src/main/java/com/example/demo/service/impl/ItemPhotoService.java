package com.example.demo.service.impl;

import com.example.demo.config.S3BucketProperties;
import com.example.demo.message.MessageHelper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;


@Service("itemPhotoService")
public final class ItemPhotoService extends BasePhotoService {
    private static final String CONTENT_DISPOSITION = "inline";

    private final S3BucketProperties s3BucketProperties;

    public ItemPhotoService(
            S3Client s3Client,
            MessageHelper messageHelper,
            S3BucketProperties s3BucketProperties
    ) {
        super(s3Client, messageHelper);
        this.s3BucketProperties = s3BucketProperties;
    }

    @Override
    protected String getFolder() {
        return s3BucketProperties.getItemFolder();
    }

    @Override
    protected String getBucketName() {
        return s3BucketProperties.getName();
    }

    @Override
    protected String getContentDisposition() {
        return CONTENT_DISPOSITION;
    }
}
