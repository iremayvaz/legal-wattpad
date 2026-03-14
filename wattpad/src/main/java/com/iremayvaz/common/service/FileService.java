package com.iremayvaz.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String saveFile(MultipartFile file, String folder); // Dosyayı kaydeder, URL döner
    void deleteFile(String fileUrl);                   // Dosyayı siler
}
