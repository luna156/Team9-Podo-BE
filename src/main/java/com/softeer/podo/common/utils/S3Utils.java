package com.softeer.podo.common.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * S3 버킷에 파일을 올리고, 올라간 S3의 uri path를 반환하는 메서드
 */
public class S3Utils {

    public static String saveFile(AmazonS3 amazonS3, String bucket, MultipartFile multipartFile) throws IOException {
        // originalFilename에 Random UUID 붙여서 같은 파일명 덮어쓰기 방지
        String originalFilename = multipartFile.getOriginalFilename() + "_" + UUID.randomUUID();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, originalFilename).toString();
    }
}
