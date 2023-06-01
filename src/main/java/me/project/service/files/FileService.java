package me.project.service.files;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import me.project.controller.FileController;
import me.project.dtos.response.bike.BikeFilesDTO;
import me.project.repository.FileRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FileService implements IFileService {
    private final FileRepository fileRepository;
    final Path uploadRoot = Paths.get("uploads");

    private String FILE_PATH(String fileName) {
        return uploadRoot + "/" + fileName;
    }

    private String NOT_FOUND(UUID fileId) {
        return "File with id" + fileId + " doesn't exists in database";
    }

    @SneakyThrows
    public UUID uploadFile(MultipartFile file) {
        final int MAX_FILE_SIZE = 1024 * 1000; // 1MB

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File is to big");
        }

        if (!Files.exists(uploadRoot)) {
            Files.createDirectories(uploadRoot);
        }

        String extension = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
        String fileName = UUID.randomUUID().toString();
        String concatedFileName = fileName + "." + extension;

        if (fileRepository.existsByFileName(concatedFileName)) {
            return fileRepository.getByFileName(concatedFileName).getFileId();
        }

        Files.copy(file.getInputStream(), uploadRoot.resolve(concatedFileName));

        me.project.entitiy.File newFile = new me.project.entitiy.File();
        newFile.setFileName(concatedFileName);

        fileRepository.save(newFile);

        return newFile.getFileId();
    }

    @SneakyThrows
    public ResponseEntity<Resource> getFile(UUID fileId) {
        me.project.entitiy.File file = fileRepository.findById(fileId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(fileId))
        );

        Path filePath = uploadRoot.resolve(file.getFileName());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Could not read the file!");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    public String getFileUrl(UUID fileId) {
        me.project.entitiy.File file = fileRepository.findById(fileId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(fileId))
        );

        return MvcUriComponentsBuilder
                .fromMethodName(FileController.class, "getFile", file.getFileId()).build().toString();
    }

    public List<BikeFilesDTO> getFilesUrls(List<me.project.entitiy.File> files) {
        return files
                .stream()
                .map(file -> {
                    String url = MvcUriComponentsBuilder
                            .fromMethodName(FileController.class, "getFile", file.getFileId()).build().toString();
                    return new BikeFilesDTO(file.getFileId(), url);
                })
                .collect(Collectors.toList());

    }

    public void deleteFile(UUID fileId) {
        me.project.entitiy.File file = getFileById(fileId);

        File fileObject = new File(FILE_PATH(file.getFileName()));

        if (!fileObject.delete()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(fileId));
        }

        fileRepository.deleteById(fileId);
    }

    public me.project.entitiy.File getFileById(UUID fileId) {
        return fileRepository.findById(fileId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(fileId))
        );
    }
}
