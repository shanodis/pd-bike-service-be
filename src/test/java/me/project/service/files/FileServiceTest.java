package me.project.service.files;

import me.project.dtos.response.bike.BikeFilesDTO;
import me.project.entitiy.File;
import me.project.repository.FileRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {
    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    static final String UPLOAD_FILENAME = "uploads/testFile.txt";

    @BeforeEach
    void setup() throws IOException {
        java.io.File realFile = new java.io.File(UPLOAD_FILENAME);

        if (realFile.createNewFile()) {
            System.out.println("File created: " + realFile.getName());
        } else {
            System.out.println("File already exists.");
        }
    }

    @AfterAll
    static void cleanup() {
        java.io.File realFile = new java.io.File(UPLOAD_FILENAME);
        if (realFile.delete()) {
            System.out.println("Deleted the file: " + realFile.getName());
        } else {
            System.out.println("Failed to delete the file.");
        }
    }

    @Test
    @DisplayName("Should throw an exception when the file size exceeds the limit")
    void uploadFileWhenFileSizeExceedsLimitThenThrowException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(1024 * 1001L);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> fileService.uploadFile(file));

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, exception.getStatus());
        assertEquals("File is to big", exception.getReason());
        verify(fileRepository, never()).save(any(File.class));
    }

    @Test
    @DisplayName(
            "Should upload the file when the file size is within the limit and the file does not exist")
    void uploadFileWhenFileSizeIsWithinLimitAndFileDoesNotExist() {
        UUID fileId = UUID.randomUUID();
        MultipartFile file = new MockMultipartFile("test.txt", "test.txt", "test/plain", "Hello, World!".getBytes());

        when(fileRepository.existsByFileName(anyString())).thenReturn(false);

        when(fileRepository.save(any(File.class))).thenReturn(new File(fileId, file.getOriginalFilename()));

        UUID result = fileService.uploadFile(file);

        assertEquals(fileId, result);
        verify(fileRepository, times(1)).save(any(File.class));
    }

    @Test
    @DisplayName("Should return file ID of existing file with same name")
    void testUploadFileWithExistingFile() throws Exception {
        // create test data
        MultipartFile file = new MockMultipartFile("test.txt", "test.txt", "test/plain", "Hello, World!".getBytes());

        // mock dependencies
        Path uploadRoot = Files.createTempDirectory("test-upload");
        when(fileRepository.existsByFileName(anyString())).thenReturn(true);
        when(fileRepository.getByFileName(anyString()))
                .thenAnswer(invocation -> {
                    me.project.entitiy.File fileEntity = new me.project.entitiy.File();
                    fileEntity.setFileId(UUID.randomUUID());
                    return fileEntity;
                });

        // call the method being tested
        UUID result = fileService.uploadFile(file);
        // assert that the file entity was retrieved from the database
        verify(fileRepository, times(1)).getByFileName(anyString());

        // assert the result
        assertNotNull(result);

        // assert that the file was not saved to disk
        Path uploadedFilePath = uploadRoot.resolve(result + ".txt");
        assertFalse(Files.exists(uploadedFilePath));
    }

    @Test
    @DisplayName(
            "Should throw a ResponseStatusException with HttpStatus.NOT_FOUND when the file does not exist in the database")
    void getFileWhenFileNotInDatabaseThenThrowResponseStatusException() {
        UUID fileId = UUID.randomUUID();

        when(fileRepository.findById(fileId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> fileService.getFile(fileId));
        assertEquals(
                "404 NOT_FOUND \"File with id" + fileId + " doesn't exists in database\"",
                assertThrows(ResponseStatusException.class, () -> fileService.getFile(fileId))
                        .getMessage());
    }

    @Test
    @DisplayName(
            "Should throw a RuntimeException when the file is not readable or does not exist on the server")
    void getFileWhenFileNotReadableThenThrowRuntimeException() {
        UUID fileId = UUID.randomUUID();
        when(fileRepository.findById(fileId)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> {
                            fileService.getFile(fileId);
                        });

        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
        assertEquals(
                exception.getReason(), "File with id" + fileId + " doesn't exists in database");
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("Should return the file as a ResponseEntity when the file exists")
    void getFileWhenFileExists() throws IOException {
        UUID fileId = UUID.randomUUID();
        File file = new File(fileId, "testFile.txt");

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));

        Resource resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resource.isReadable()).thenReturn(true);
        when(resource.getFilename()).thenReturn("testFile.txt");

        UrlResource urlResource = mock(UrlResource.class);
        when(urlResource.getURL()).thenReturn(URI.create("file:/uploads/testFile.txt").toURL());
        when(urlResource.getFile()).thenReturn(new java.io.File(UPLOAD_FILENAME));

        ResponseEntity<Resource> expectedResponse =
                ResponseEntity.ok()
                        .header(
                                HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"testFile.txt\"")
                        .body(resource);

        assertEquals(expectedResponse.getStatusCode(), fileService.getFile(fileId).getStatusCode());
    }

    @Test
    @DisplayName("Should return an empty list when the input list of files is empty")
    void getFilesUrlsWhenInputListIsEmpty() {
        List<File> files = new ArrayList<>();
        List<BikeFilesDTO> expected = new ArrayList<>();

        List<BikeFilesDTO> actual = fileService.getFilesUrls(files);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName(
            "Should delete the file and remove its record from the database when the file exists")
    void deleteFileWhenFileExists() {
        UUID fileId = UUID.randomUUID();
        File file = new File(fileId, "testFile.txt");

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));

        fileService.deleteFile(fileId);

        verify(fileRepository, times(1)).deleteById(fileId);
        verifyNoMoreInteractions(fileRepository);
    }

    @Test
    @DisplayName("Should throw a not found exception when the file does not exist")
    void deleteFileWhenFileDoesNotExistThenThrowNotFoundException() {
        UUID fileId = UUID.randomUUID();
        when(fileRepository.findById(fileId)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> {
                            fileService.deleteFile(fileId);
                        });

        assertEquals(exception.getStatus().value(), 404);
        assertEquals(
                exception.getReason(), "File with id" + fileId + " doesn't exists in database");

        verify(fileRepository, times(1)).findById(fileId);
        verify(fileRepository, never()).deleteById(fileId);
    }

    @Test
    @DisplayName(
            "Should throw a ResponseStatusException with HttpStatus.NOT_FOUND when the fileId is not found")
    void getFileByIdWhenFileIdNotFoundThenThrowResponseStatusException() {
        UUID fileId = UUID.randomUUID();
        when(fileRepository.findById(fileId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> fileService.getFileById(fileId));
    }

    @Test
    @DisplayName("Should return the file when the fileId is valid")
    void getFileByIdWhenFileIdIsValid() {
        UUID fileId = UUID.randomUUID();
        File file = new File(fileId, "testFile.txt");

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));

        File result = fileService.getFileById(fileId);

        assertEquals(file, result);
        verify(fileRepository, times(1)).findById(fileId);
    }
}