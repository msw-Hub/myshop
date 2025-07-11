package com.example.myShop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {
        // Universally Unique Identifier로 개체 구별하기 위해 이름 부여
        UUID uuid = UUID.randomUUID();

        // 파일 확장자 추출
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        // 저장될 파일 이름 생성
        String savedFileName = uuid.toString() + extension;

        // 저장될 전체 경로
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;

        // 파일 저장
        try (FileOutputStream fos = new FileOutputStream(fileUploadFullUrl)) {
            fos.write(fileData);
        }

        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception {
        File deleteFile = new File(filePath);

        if (deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
