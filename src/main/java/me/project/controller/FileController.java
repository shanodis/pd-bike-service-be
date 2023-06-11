package me.project.controller;

import me.project.service.files.IFileService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/files")
@AllArgsConstructor
public class FileController {
    private final IFileService fileService;

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CLIENT"})
    @GetMapping("{fileId}")
    public String getFileUrl(@PathVariable UUID fileId) {
        return fileService.getFileUrl(fileId);
    }

    @GetMapping("{fileId}/download")
    public ResponseEntity<Resource> getFile(@PathVariable UUID fileId) {
        return fileService.getFile(fileId);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @PostMapping(value = "/rest/upload", consumes = {"multipart/form-data"})
    public UUID uploadFile(@RequestParam("file")MultipartFile file) {
        return fileService.uploadFile(file);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @DeleteMapping("{fileId}")
    public void deleteFile(@PathVariable UUID fileId) {
        fileService.deleteFile(fileId);
    }
}
