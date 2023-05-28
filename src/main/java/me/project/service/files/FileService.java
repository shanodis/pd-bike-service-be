package me.project.service.files;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import me.project.config.AmazonConfig;
import me.project.entitiy.File;
import me.project.repository.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileService implements IFileService {
    private final AmazonS3 s3client;
    private final AmazonConfig amazonConfig;
    private final FileRepository fileRepository;

    private String NOT_FOUND(UUID fileId) {
        return "File with id" + fileId + " doesn't exists in database";
    }
    private String TO_BIG() {
        return "File is to big";
    }

    @Override
    public UUID uploadFile(MultipartFile file) {

        if (file.getSize() > 2000000) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, TO_BIG());
        }

        String extension = file.getOriginalFilename().split("\\.")[1];
        String fileName = UUID.randomUUID().toString();
        String concatedFileName = fileName + "." +  extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            s3client.putObject(amazonConfig.bucketName, concatedFileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File newFile = new File();
        newFile.setFileName(concatedFileName);

        fileRepository.save(newFile);

        return newFile.getFileId();
    }

    @Override
    public String getFileUrl(UUID fileId) {
        File file = fileRepository.findById(fileId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(fileId))
        );

        return "https://bike-be-files.s3.eu-central-1.amazonaws.com/" + file.getFileName();

    }

    @Override
    public void deleteFile(UUID fileId) {

        File file = fileRepository.findById(fileId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(fileId))
        );

        s3client.deleteObject(amazonConfig.bucketName, file.getFileName());

        fileRepository.deleteById(fileId);
    }
}
