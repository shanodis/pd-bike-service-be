package me.project.service.bike;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.bike.BikeUpdateRequestDTO;
import me.project.dtos.response.DictionaryResponseDTO;
import me.project.dtos.response.bike.BikeFilesDTO;
import me.project.dtos.response.bike.BikeResponseDTO;
import me.project.dtos.response.bikes.SimpleBikeDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.entitiy.*;
import me.project.repository.*;
import me.project.search.specificator.Specifications;
import me.project.service.files.IFileService;
import me.project.service.user.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BikeService")
class BikeServiceTest {

    @Mock
    private IFileService fileService;
    @Mock
    private BikeRepository bikeRepository;
    @Mock
    private OrderServiceRepository orderServiceRepository;
    @Mock
    private OrderPartRepository orderPartRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private IUserService userService;
    @Mock
    private BikeFileRepository bikeFileRepository;

    @InjectMocks
    private BikeService bikeService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(UUID.randomUUID());
    }

    @Test
    @DisplayName(
            "Should throw a ResponseStatusException with HttpStatus.NOT_FOUND when the bikeId is not found")
    void getBikeWhenBikeIdNotFoundThenThrowResponseStatusException() {
        UUID bikeId = UUID.randomUUID();
        when(bikeRepository.findById(bikeId)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> {
                            bikeService.getBike(bikeId);
                        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Should return the bike when the bikeId is valid")
    void getBikeWhenBikeIdIsValid() {
        UUID bikeId = UUID.randomUUID();
        Bike bike = new Bike(user, "BikeName", "BikeMake", "BikeModel", "SerialNumber", 2021);
        bike.setBikeId(bikeId);

        when(bikeRepository.findById(bikeId)).thenReturn(Optional.of(bike));

        BikeResponseDTO bikeResponseDTO = bikeService.getBike(bikeId);

        assertEquals(bike.getBikeName(), bikeResponseDTO.getBikeName());
        assertEquals(bike.getBikeMake(), bikeResponseDTO.getBikeMake());
        assertEquals(bike.getBikeModel(), bikeResponseDTO.getBikeModel());
        assertEquals(bike.getSerialNumber(), bikeResponseDTO.getSerialNumber());
        assertEquals(bike.getYearOfProduction(), bikeResponseDTO.getYearOfProduction());

        verify(bikeRepository, times(1)).findById(bikeId);
    }

    @Test
    @DisplayName("Should return an empty list when no bikes match the user and phrase criteria")
    void getBikesByUserAndPhraseReturnsEmptyList() {
        // create test data
        UUID userId = UUID.randomUUID();
        String phrase = "Bike";

        when(bikeRepository.findAll(any(Specifications.class))).thenReturn(new ArrayList<>());

        List<SimpleBikeDTO> result = bikeService.getBikesByUserAndPhrase(userId, phrase);

        assertEquals(Collections.emptyList(), result);
        verify(bikeRepository, times(1)).findAll(any(Specifications.class));
    }

    @Test
    @DisplayName("Should return all bikes matching the phrase when user is null")
    void getBikesByUserAndPhraseWithNullUser() {
        // create test data
        UUID userId = UUID.randomUUID();
        String phrase = "Bike";

        UUID bikeId1 = UUID.randomUUID();
        UUID bikeId2 = UUID.randomUUID();
        UUID bikeId3 = UUID.randomUUID();

        Bike bike1 = new Bike(user, "Bike1", "Make1", "Model1", "SN1", 2021);
        bike1.setBikeId(bikeId1);
        bike1.setBikeFiles(new ArrayList<>());

        Bike bike2 = new Bike(user, "Bike2", "Make2", "Model2", "SN2", 2020);
        bike2.setBikeId(bikeId2);
        bike2.setBikeFiles(new ArrayList<>());

        Bike bike3 = new Bike(user, "Bike3", "Make3", "Model3", "SN3", 2019);
        bike3.setBikeId(bikeId3);
        bike3.setBikeFiles(new ArrayList<>());

        List<Bike> bikes = Arrays.asList(bike1, bike2, bike3);

        when(bikeRepository.findAll(any(Specifications.class))).thenReturn(bikes);

        when(fileService.getFilesUrls(anyList())).thenReturn(Collections.emptyList());

        // call service method
        List<SimpleBikeDTO> result = bikeService.getBikesByUserAndPhrase(null, phrase);

        // assert results
        assertEquals(3, result.size());
        assertEquals("Bike1", result.get(0).getBikeName());
        assertEquals("Bike2", result.get(1).getBikeName());
        assertEquals("Bike3", result.get(2).getBikeName());
    }

    @Test
    @DisplayName("Should return all bikes for a user when phrase is null")
    void getBikesByUserAndPhraseWithNullPhrase() {
        // create test data
        UUID userId = UUID.randomUUID();

        UUID bikeId1 = UUID.randomUUID();
        UUID bikeId2 = UUID.randomUUID();
        UUID bikeId3 = UUID.randomUUID();

        Bike bike1 = new Bike(user, "Bike1", "Make1", "Model1", "SN1", 2021);
        bike1.setBikeId(bikeId1);
        bike1.setBikeFiles(new ArrayList<>());

        Bike bike2 = new Bike(user, "Bike2", "Make2", "Model2", "SN2", 2020);
        bike2.setBikeId(bikeId2);
        bike2.setBikeFiles(new ArrayList<>());

        Bike bike3 = new Bike(user, "Bike3", "Make3", "Model3", "SN3", 2019);
        bike3.setBikeId(bikeId3);
        bike3.setBikeFiles(new ArrayList<>());

        List<Bike> bikes = Arrays.asList(bike1, bike2, bike3);

        when(userService.getUser(userId)).thenReturn(user);

        when(bikeRepository.findAll(any(Specifications.class))).thenReturn(bikes);

        when(fileService.getFilesUrls(anyList())).thenReturn(Collections.emptyList());

        // call service method
        List<SimpleBikeDTO> result = bikeService.getBikesByUserAndPhrase(userId, null);

        // assert results
        assertEquals(3, result.size());
        assertEquals("Bike1", result.get(0).getBikeName());
        assertEquals("Bike2", result.get(1).getBikeName());
        assertEquals("Bike3", result.get(2).getBikeName());
    }

    @Test
    @DisplayName("Should return a list of bikes filtered by user and phrase")
    void testGetBikesByUserAndPhrase() {
        // create test data
        UUID userId = UUID.randomUUID();
        String phrase = "Bike";

        UUID bikeId1 = UUID.randomUUID();
        UUID bikeId2 = UUID.randomUUID();
        UUID bikeId3 = UUID.randomUUID();

        Bike bike1 = new Bike(user, "Bike1", "Make1", "Model1", "SN1", 2021);
        bike1.setBikeId(bikeId1);
        bike1.setBikeFiles(new ArrayList<>());

        Bike bike2 = new Bike(user, "Bike2", "Make2", "Model2", "SN2", 2020);
        bike2.setBikeId(bikeId2);
        bike2.setBikeFiles(new ArrayList<>());

        Bike bike3 = new Bike(user, "Bike3", "Make3", "Model3", "SN3", 2019);
        bike3.setBikeId(bikeId3);
        bike3.setBikeFiles(new ArrayList<>());

        List<Bike> bikes = Arrays.asList(bike1, bike2, bike3);

        when(userService.getUser(userId)).thenReturn(user);

        when(bikeRepository.findAll(any(Specifications.class))).thenReturn(bikes);

        when(fileService.getFilesUrls(anyList())).thenReturn(Collections.emptyList());

        // call the method being tested
        List<SimpleBikeDTO> result = bikeService.getBikesByUserAndPhrase(userId, phrase);

        // assert the result
        // assert results
        assertEquals(3, result.size());
        assertEquals("Bike1", result.get(0).getBikeName());
        assertEquals("Bike2", result.get(1).getBikeName());
        assertEquals("Bike3", result.get(2).getBikeName());
    }

    @Test
    @DisplayName("Should throw a ResponseStatusException when bikeId is not found")
    void getBikeFilesUrlsWhenBikeIdNotFoundThenThrowException() {
        UUID bikeId = UUID.randomUUID();

        when(bikeRepository.findById(bikeId)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> {
                            bikeService.getBikeFilesUrls(bikeId);
                        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Should return a list of BikeFilesDTO when bikeId is valid")
    void getBikeFilesUrlsWhenBikeIdIsValid() { // create a mock Bike object
        Bike bike = new Bike();
        bike.setBikeId(UUID.randomUUID());
        bike.setBikeFiles(new ArrayList<>());

        // create a mock BikeFile object
        BikeFile bikeFile = new BikeFile();
        bikeFile.setBikeFileId(UUID.randomUUID());
        bikeFile.setBike(bike);
        bikeFile.setOrderNumber(1);

        // create a mock File object
        File file = new File();
        file.setFileId(UUID.randomUUID());
        file.setFileName("test.jpg");

        // create a mock BikeFilesDTO object
        BikeFilesDTO bikeFilesDTO = new BikeFilesDTO();
        bikeFilesDTO.setFileId(file.getFileId());
        bikeFilesDTO.setFileUrl("http://localhost:8080/files/" + file.getFileId());

        // create a list of BikeFile objects
        List<BikeFile> bikeFiles = new ArrayList<>();
        bikeFiles.add(bikeFile);
        bike.setBikeFiles(bikeFiles);

        // create a list of BikeFilesDTO objects
        List<BikeFilesDTO> bikeFilesDTOs = new ArrayList<>();
        bikeFilesDTOs.add(bikeFilesDTO);

        // mock the behavior of bikeRepository.findById()
        when(bikeRepository.findById(bike.getBikeId())).thenReturn(Optional.of(bike));

        // mock the behavior of fileService.getFilesUrls()
        when(fileService.getFilesUrls(any())).thenReturn(bikeFilesDTOs);

        // call the method under test
        List<BikeFilesDTO> result = bikeService.getBikeFilesUrls(bike.getBikeId());

        // verify the result
        assertEquals(bikeFilesDTOs, result);

        // verify that bikeRepository.findById() was called once with the correct argument
        verify(bikeRepository, times(1)).findById(bike.getBikeId());

        assertEquals(bike.getBikeFiles(), bikeFiles);

        // verify that fileService.getFilesUrls() was called once with the correct argument
        verify(fileService, times(1)).getFilesUrls(any());
    }

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    @DisplayName("Should return an empty page of bikes dictionary when no bikes are found")
    void getBikesDictionaryWhenNoBikesFound() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "bikeName");
        UUID userId = UUID.randomUUID();
        when(bikeRepository.findAll(pageRequestDTO.getRequest(Bike.class)))
                .thenReturn(Page.empty());

        PageResponse<DictionaryResponseDTO> result =
                bikeService.getBikesDictionary(pageRequestDTO, userId);

        assertEquals(0, result.getContent().size());
        assertEquals(1, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    @DisplayName("Should return a page of bikes dictionary when userId is not provided")
    void getBikesDictionaryWhenUserIdIsNotProvided() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "bikeName");
        List<Bike> bikes = new ArrayList<>();
        Bike bike1 = new Bike(user, "Bike1", "Make1", "Model1", "SN1", 2021);
        Bike bike2 = new Bike(user, "Bike2", "Make2", "Model2", "SN2", 2020);
        bikes.add(bike1);
        bikes.add(bike2);
        Page<Bike> bikePage = new PageImpl<>(bikes);

        when(bikeRepository.findAll(pageRequestDTO.getRequest(Bike.class))).thenReturn(bikePage);

        PageResponse<DictionaryResponseDTO> result =
                bikeService.getBikesDictionary(pageRequestDTO, null);

        assertEquals(2, result.getContent().size());
        assertEquals("Bike1", result.getContent().get(0).getName());
        assertEquals("Bike2", result.getContent().get(1).getName());
        assertEquals(1, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    @DisplayName("Should return a page of bikes dictionary when userId is provided")
    void getBikesDictionaryWhenUserIdIsProvided() { // Create test data
        UUID userId = UUID.randomUUID();
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "bikeName");
        List<Bike> bikes = new ArrayList<>();
        bikes.add(new Bike(user, "Bike1", "Make1", "Model1", "SN1", 2021));
        bikes.add(new Bike(user, "Bike2", "Make2", "Model2", "SN2", 2020));
        bikes.add(new Bike(user, "Bike3", "Make3", "Model3", "SN3", 2019));
        Page<Bike> bikePage = new PageImpl<>(bikes);

        // Set up mock behavior
        when(bikeRepository.findAll(any(Specifications.class), any(Pageable.class)))
                .thenReturn(bikePage);

        // Call method under test
        PageResponse<DictionaryResponseDTO> result =
                bikeService.getBikesDictionary(pageRequestDTO, userId);

        // Verify mock behavior
        verify(bikeRepository, times(1)).findAll(any(Specifications.class), any(Pageable.class));

        // Assert result
        assertEquals(3, result.getContent().size());
        assertEquals("Bike1", result.getContent().get(0).getName());
        assertEquals("Bike2", result.getContent().get(1).getName());
        assertEquals("Bike3", result.getContent().get(2).getName());
    }

    @Test
    @DisplayName("Should throw a not found exception when the bike does not exist")
    void addBikeFileWhenBikeDoesNotExistThenThrowNotFoundException() {
        UUID bikeId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);

        when(bikeRepository.findById(bikeId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> bikeService.addBikeFile(bikeId, 1, file));

        verify(bikeRepository, times(1)).findById(bikeId);
        verifyNoMoreInteractions(bikeRepository);
        verifyNoInteractions(fileService, bikeFileRepository);
    }

    @Test
    @DisplayName("Should return the uploaded file ID when the bike file is already added")
    void addBikeFileWhenBikeFileAlreadyAdded() {
        UUID bikeId = UUID.randomUUID();
        Integer orderNumber = 1;
        MultipartFile multipartFile = mock(MultipartFile.class);
        UUID fileId = UUID.randomUUID();

        Bike bike = new Bike();
        bike.setBikeId(bikeId);

        File file = new File();
        file.setFileId(fileId);
        file.setFileName("test.txt");

        BikeFile bikeFile = new BikeFile(bike, file, orderNumber);
        List<BikeFile> bikeFiles = new ArrayList<>();
        bikeFiles.add(bikeFile);
        bike.setBikeFiles(bikeFiles);

        when(bikeRepository.findById(bikeId)).thenReturn(Optional.of(bike));
        when(fileService.uploadFile(multipartFile)).thenReturn(file.getFileId());

        UUID result = bikeService.addBikeFile(bikeId, orderNumber, multipartFile);

        verify(fileService, times(1)).uploadFile(multipartFile);
        verify(bikeFileRepository, times(0)).save(any(BikeFile.class));

        assertEquals(fileId, result);
    }

    @Test
    @DisplayName("Should add a bike file when the bike exists and the file is not already added")
    void addBikeFileWhenBikeExistsAndFileNotAdded() {
        UUID bikeId = UUID.randomUUID();
        Integer orderNumber = 1;
        MultipartFile multipartFile = mock(MultipartFile.class);
        UUID fileId = UUID.randomUUID();

        Bike bike = new Bike();
        bike.setBikeId(bikeId);
        bike.setUser(user);

        File file = new File(fileId, multipartFile.getName());
        file.setFileId(fileId);
        file.setFileName("test.txt");

        BikeFile bikeFile = new BikeFile(bike, file, orderNumber);
        List<BikeFile> bikeFiles = new ArrayList<>();
        bikeFiles.add(bikeFile);
        bike.setBikeFiles(bikeFiles);

        when(bikeRepository.findById(bikeId)).thenReturn(Optional.of(bike));
        when(fileService.uploadFile(multipartFile)).thenReturn(fileId);

        bikeService.addBikeFile(bikeId, orderNumber, multipartFile);

        verify(bikeRepository, times(1)).findById(bikeId);
        verify(fileService, times(1)).uploadFile(multipartFile);
        verify(bikeFileRepository, times(0)).save(any(BikeFile.class));
    }

    @Test
    @DisplayName("Should throw an exception when the bike ID is not found")
    void updateBikeFileWhenBikeIdNotFoundThenThrowException() {
        UUID bikeId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);

        when(bikeRepository.findById(bikeId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> bikeService.updateBikeFile(bikeId, fileId, file));

        verify(bikeRepository, times(1)).findById(bikeId);
        verifyNoMoreInteractions(bikeRepository);
    }

    @Test
    @DisplayName("Should update the bike file when the bike and file IDs are valid")
    void updateBikeFileWhenBikeAndFileIdsAreValid() {
        UUID bikeId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        MultipartFile multipartFile = mock(MultipartFile.class);

        Bike bike = new Bike();
        bike.setBikeId(bikeId);

        File file = new File();
        file.setFileId(fileId);
        file.setFileName("test.txt");

        BikeFile bikeFile = new BikeFile(bike, file, 1);
        List<BikeFile> bikeFiles = new ArrayList<>();
        bikeFiles.add(bikeFile);
        bike.setBikeFiles(bikeFiles);

        when(bikeRepository.findById(bikeId)).thenReturn(Optional.of(bike));

        bikeService.updateBikeFile(bikeId, fileId, multipartFile);

        verify(fileService, times(1)).deleteFile(fileId);
        verify(fileService, times(1)).uploadFile(multipartFile);
        verify(bikeFileRepository, times(2)).save(bikeFile);
    }

    @Test
    @DisplayName("Should throw an exception when the file ID is not found")
    void updateBikeFileWhenFileIdNotFoundThenThrowException() {
        UUID bikeId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);

        Optional<Bike> optionalBike = Optional.empty();
        when(bikeRepository.findById(bikeId)).thenReturn(optionalBike);

        assertThrows(ResponseStatusException.class, () -> bikeService.updateBikeFile(bikeId, fileId, file));

        verify(bikeRepository, times(1)).findById(bikeId);
        verifyNoMoreInteractions(bikeRepository);
        verifyNoInteractions(fileService);
    }

    @Test
    @DisplayName("Should throw a not found exception when the bike ID is not found")
    void deleteBikeFileWhenBikeIdNotFoundThenThrowException() {
        UUID bikeId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();

        when(bikeRepository.findById(bikeId)).thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class, () -> bikeService.deleteBikeFile(bikeId, fileId));

        verify(bikeRepository, times(1)).findById(bikeId);
        verifyNoMoreInteractions(bikeRepository);
        verifyNoInteractions(fileService, bikeFileRepository);
    }

    @Test
    @DisplayName("Should throw a not found exception when the file ID is not found")
    void deleteBikeFileWhenFileIdNotFoundThenThrowException() {
        UUID bikeId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();

        Bike bike = new Bike();
        bike.setBikeId(bikeId);
        bike.setBikeFiles(new ArrayList<>());

        when(bikeRepository.findById(bikeId)).thenReturn(Optional.of(bike));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> {
                            bikeService.deleteBikeFile(bikeId, fileId);
                        });

        verify(bikeRepository, times(1)).findById(bikeId);
        verifyNoMoreInteractions(bikeRepository, bikeFileRepository, fileService);

        String expectedMessage = String.format("Bike file with file of id %s not found", fileId);
        String actualMessage = exception.getReason();
        assert (Objects.requireNonNull(actualMessage).contains(expectedMessage));
        assert (exception.getStatus().value() == 404);
    }

    @Test
    @DisplayName("Should delete the bike file when the bike and file IDs are valid")
    void deleteBikeFileWhenBikeAndFileIdsAreValid() {
        UUID bikeId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();

        Bike bike = new Bike(user, "BikeName", "BikeMake", "BikeModel", "SerialNumber", 2021);
        bike.setBikeId(bikeId);

        List<BikeFile> bikeFiles = new ArrayList<>();
        BikeFile bikeFile = new BikeFile(bike, new File(fileId, "FileName"), 1);
        bikeFiles.add(bikeFile);
        bike.setBikeFiles(bikeFiles);

        when(bikeRepository.findById(bikeId)).thenReturn(Optional.of(bike));

        bikeService.deleteBikeFile(bikeId, fileId);

        verify(bikeFileRepository, times(1)).delete(bikeFile);
        verify(fileService, times(1)).deleteFile(fileId);
    }

    @Test
    @DisplayName("Should throw a not found exception when invalid bikeId is provided")
    void updateBikeWithInvalidBikeIdThrowsNotFoundException() {
        UUID invalidBikeId = UUID.randomUUID();
        BikeUpdateRequestDTO request = new BikeUpdateRequestDTO();
        request.setBikeName("Updated Bike Name");

        when(bikeRepository.findById(invalidBikeId)).thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> bikeService.updateBike(invalidBikeId, request));

        verify(bikeRepository, times(1)).findById(invalidBikeId);
        verify(bikeRepository, never()).save(any(Bike.class));
    }

    @Test
    @DisplayName("Should update the bike when valid bikeId and request are provided")
    void updateBikeWithValidBikeIdAndRequest() { // create a bike object
        Bike bike = new Bike(user, "BikeName", "BikeMake", "BikeModel", "SerialNumber", 2021);
        bike.setBikeId(UUID.randomUUID());

        // create a bike update request DTO
        BikeUpdateRequestDTO request = new BikeUpdateRequestDTO();
        request.setBikeName("NewBikeName");
        request.setBikeMake("NewBikeMake");
        request.setBikeModel("NewBikeModel");
        request.setSerialNumber("NewSerialNumber");
        request.setYearOfProduction(2022);

        // mock the bike repository to return the bike object when findById is called
        when(bikeRepository.findById(bike.getBikeId())).thenReturn(Optional.of(bike));

        // call the updateBike method with the bikeId and request
        bikeService.updateBike(bike.getBikeId(), request);

        // verify that the bikeRepository's save method was called once with the updated bike object
        verify(bikeRepository, times(1)).save(bike);
    }

    @Test
    @DisplayName(
            "Should throw a ResponseStatusException with HttpStatus.NOT_FOUND when the bikeId is not found")
    void deleteBikeWhenBikeIdNotFoundThenThrowResponseStatusException() {
        UUID bikeId = UUID.randomUUID();

        when(bikeRepository.findById(bikeId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> bikeService.deleteBike(bikeId));

        verify(bikeRepository, times(1)).findById(bikeId);
        verifyNoMoreInteractions(
                bikeRepository,
                orderServiceRepository,
                orderPartRepository,
                orderRepository,
                bikeFileRepository);
    }

    @Test
    @DisplayName("Should delete the bike and its related data when the bikeId is valid")
    void deleteBikeWhenBikeIdIsValid() {
        UUID bikeId = UUID.randomUUID();

        Bike bike = new Bike(user, "Bike1", "Make1", "Model1", "SN1", 2021);
        bike.setBikeId(bikeId);

        List<Order> orders = new ArrayList<>();
        Order order1 = new Order("Description1", LocalDateTime.now(), bike, user, new OrderStatus(UUID.randomUUID(), "in progress"));
        Order order2 = new Order("Description2", LocalDateTime.now(), bike, user, new OrderStatus(UUID.randomUUID(), "done"));
        orders.add(order1);
        orders.add(order2);

        bike.setOrders(orders);

        when(bikeRepository.findById(bikeId)).thenReturn(Optional.of(bike));

        bikeService.deleteBike(bikeId);

        verify(bikeRepository, times(1)).findById(bikeId);
        verify(bikeRepository, times(1)).save(bike);
        verify(orderServiceRepository, times(orders.size())).deleteAll(any());
        verify(orderPartRepository, times(orders.size())).deleteAll(any());
        verify(orderRepository, times(1)).deleteAll(any());
        verify(bikeRepository, times(1)).deleteById(bikeId);
    }
}