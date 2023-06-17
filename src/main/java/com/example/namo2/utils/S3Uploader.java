package com.example.namo2.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public  void  uploadFile(InputStream inputStream, ObjectMetadata objectMeTadata, String fileName){
        //.withCannedAcl(CannedAccessControlList.PublicRead)); 는 파일을 누구나 읽게 해준다.
        amazonS3Client.putObject(new PutObjectRequest(bucket,fileName,inputStream,objectMeTadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public  String getFileUrl(String fileName){
        return amazonS3Client.getUrl(bucket,fileName).toString();
    }

    public void delete(String key) {
        //Delete 객체 생성
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, key);
        //Delete
        amazonS3Client.deleteObject(deleteObjectRequest);
    }
}
