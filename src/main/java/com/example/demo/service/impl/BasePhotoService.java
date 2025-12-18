package com.example.demo.service.impl;

import com.example.demo.dto.response.FileResponse;
import com.example.demo.exception.DeleteProtoException;
import com.example.demo.exception.FileDownloadException;
import com.example.demo.exception.UploadPhotoException;
import com.example.demo.message.MessageHelper;
import com.example.demo.service.PhotoService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class BasePhotoService implements PhotoService {
    S3Client s3Client;
    MessageHelper messageHelper;

    protected abstract String getFolder();

    protected abstract String getBucketName();

    protected abstract String getContentDisposition();

    /**
     * построение ключа - может быть переопределен
     */
    protected String buildKey(String entityId, String filename) {
        return getFolder() + "/" + entityId + "/" + filename;
    }

    @Override
    public String uploadPhoto(MultipartFile photo, String entityKey) {
        String key = buildKey(entityKey, photo.getOriginalFilename());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(getBucketName())
                .key(key)
                .contentType(photo.getContentType())
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(photo.getBytes()));
        } catch (IOException e) {
            throw new UploadPhotoException(messageHelper.get("s3.upload.file.exception"));
        }
        return key;
    }

    @Override
    public FileResponse downloadPhoto(String photoKey) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(getBucketName())
                .key(photoKey)
                .build();

        try (ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(objectRequest)) {
            byte[] data = inputStream.readAllBytes();

            var headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, getContentDisposition());
            headers.add(HttpHeaders.CONTENT_TYPE, inputStream.response().contentType());

            return new FileResponse(data, headers);

        } catch (IOException e) {
            throw new FileDownloadException(messageHelper.get("s3.download.file.exception"));
        }
    }

    @Override
    public void deletePhoto(String photoKey) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(getBucketName())
                    .key(photoKey)
                    .build());
        } catch (Exception e) {
            throw new DeleteProtoException(messageHelper.get("s3.delete.file.exception"));
        }
    }
}
