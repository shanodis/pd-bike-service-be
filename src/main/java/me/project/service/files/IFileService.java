package me.project.service.files;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IFileService {

    UUID uploadFile(MultipartFile file);

    String getFileUrl(UUID fileId);

    void deleteFile(UUID fileId);
}
