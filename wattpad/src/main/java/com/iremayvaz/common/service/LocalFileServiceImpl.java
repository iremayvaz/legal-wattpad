package com.iremayvaz.common.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalFileServiceImpl implements FileService {

    private final String uploadDir = "uploads/";

    @Override
    public String saveFile(MultipartFile file, String folder) {
        try {
            // Klasör yoksa oluştur
            Path path = Paths.get(uploadDir + folder);
            if (!Files.exists(path)) Files.createDirectories(path);

            // Dosya ismini benzersiz yap (unique name)
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = path.resolve(fileName);

            // Dosyayı diske yaz
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/" + folder + "/" + fileName; // Veritabanına kaydedilecek URL
        } catch (IOException e) {
            throw new RuntimeException("Dosya yüklenemedi: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileUrl) {

    }
}
