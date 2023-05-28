package me.project.service.files;

import me.project.dtos.response.bike.BikeFilesDTO;
import me.project.entitiy.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IFileService {

    UUID uploadFile(MultipartFile file);

    String getFileUrl(UUID fileId);

    File getFileById(UUID fileId);

    List<BikeFilesDTO> getFilesUrls(List<File> files);

    void deleteFile(UUID fileId);
}
