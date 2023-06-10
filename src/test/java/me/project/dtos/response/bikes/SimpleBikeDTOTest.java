package me.project.dtos.response.bikes;

import me.project.dtos.response.bike.BikeFilesDTO;
import me.project.entitiy.Bike;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("SimpleBikeDTO should")
class SimpleBikeDTOTest {

    @Test
    @DisplayName("Should convert Bike entity to SimpleBikeDTO with empty bikePhotosUrls")
    void convertFromEntityWithEmptyBikePhotosUrls() {        // Create a mock Bike object
        Bike bike = mock(Bike.class);
        UUID bikeId = UUID.randomUUID();
        String bikeName = "Test Bike";
        String bikeMake = "Test Make";
        String bikeModel = "Test Model";
        String serialNumber = "123456";
        Integer yearOfProduction = 2021;

        // Set up the mock Bike object
        when(bike.getBikeId()).thenReturn(bikeId);
        when(bike.getBikeName()).thenReturn(bikeName);
        when(bike.getBikeMake()).thenReturn(bikeMake);
        when(bike.getBikeModel()).thenReturn(bikeModel);
        when(bike.getSerialNumber()).thenReturn(serialNumber);
        when(bike.getYearOfProduction()).thenReturn(yearOfProduction);

        // Create an empty list of BikeFilesDTO
        List<BikeFilesDTO> bikePhotosUrls = new ArrayList<>();

        // Call the convertFromEntity method
        SimpleBikeDTO simpleBikeDTO = SimpleBikeDTO.convertFromEntity(bike, bikePhotosUrls);

        // Assert that the SimpleBikeDTO object was created correctly
        assertEquals(bikeId, simpleBikeDTO.getBikeId());
        assertEquals(bikeName, simpleBikeDTO.getBikeName());
        assertEquals(bikeMake, simpleBikeDTO.getBikeMake());
        assertEquals(bikeModel, simpleBikeDTO.getBikeModel());
        assertEquals(serialNumber, simpleBikeDTO.getSerialNumber());
        assertEquals(yearOfProduction, simpleBikeDTO.getYearOfProduction());
        assertEquals(bikePhotosUrls, simpleBikeDTO.getBikePhotosUrls());
    }

    @Test
    @DisplayName("Should convert Bike entity to SimpleBikeDTO with null bikePhotosUrls")
    void convertFromEntityWithNullBikePhotosUrls() {// Create a mock Bike object
        Bike bike = mock(Bike.class);
        UUID bikeId = UUID.randomUUID();
        String bikeName = "Test Bike";
        String bikeMake = "Test Make";
        String bikeModel = "Test Model";
        String serialNumber = "123456";
        Integer yearOfProduction = 2021;

        // Set up the mock Bike object
        when(bike.getBikeId()).thenReturn(bikeId);
        when(bike.getBikeName()).thenReturn(bikeName);
        when(bike.getBikeMake()).thenReturn(bikeMake);
        when(bike.getBikeModel()).thenReturn(bikeModel);
        when(bike.getSerialNumber()).thenReturn(serialNumber);
        when(bike.getYearOfProduction()).thenReturn(yearOfProduction);

        // Create a mock List of BikeFilesDTO objects
        List<BikeFilesDTO> bikePhotosUrls = new ArrayList<>();

        // Call the convertFromEntity method
        SimpleBikeDTO simpleBikeDTO = SimpleBikeDTO.convertFromEntity(bike, bikePhotosUrls);

        // Verify that the SimpleBikeDTO object was created correctly
        assertEquals(bikeId, simpleBikeDTO.getBikeId());
        assertEquals(bikeName, simpleBikeDTO.getBikeName());
        assertEquals(bikeMake, simpleBikeDTO.getBikeMake());
        assertEquals(bikeModel, simpleBikeDTO.getBikeModel());
        assertEquals(serialNumber, simpleBikeDTO.getSerialNumber());
        assertEquals(yearOfProduction, simpleBikeDTO.getYearOfProduction());
        assertEquals(bikePhotosUrls, simpleBikeDTO.getBikePhotosUrls());
    }

    @Test
    @DisplayName("Should convert Bike entity to SimpleBikeDTO with correct fields")
    void convertFromEntityWithCorrectFields() {// Create a mock Bike object
        Bike bike = mock(Bike.class);
        UUID bikeId = UUID.randomUUID();
        String bikeName = "Test Bike";
        String bikeMake = "Test Make";
        String bikeModel = "Test Model";
        String serialNumber = "123456";
        Integer yearOfProduction = 2021;

        // Set up the mock Bike object
        when(bike.getBikeId()).thenReturn(bikeId);
        when(bike.getBikeName()).thenReturn(bikeName);
        when(bike.getBikeMake()).thenReturn(bikeMake);
        when(bike.getBikeModel()).thenReturn(bikeModel);
        when(bike.getSerialNumber()).thenReturn(serialNumber);
        when(bike.getYearOfProduction()).thenReturn(yearOfProduction);

        // Create a mock BikeFilesDTO object
        BikeFilesDTO bikeFilesDTO = mock(BikeFilesDTO.class);
        UUID fileId = UUID.randomUUID();
        String fileUrl = "http://test.com/image.jpg";

        // Set up the mock BikeFilesDTO object
        when(bikeFilesDTO.getFileId()).thenReturn(fileId);
        when(bikeFilesDTO.getFileUrl()).thenReturn(fileUrl);

        // Create a list of BikeFilesDTO objects
        List<BikeFilesDTO> bikeFilesDTOList = new ArrayList<>();
        bikeFilesDTOList.add(bikeFilesDTO);

        // Call the method being tested
        SimpleBikeDTO simpleBikeDTO = SimpleBikeDTO.convertFromEntity(bike, bikeFilesDTOList);

        // Verify that the SimpleBikeDTO object has the correct fields
        assertEquals(bikeId, simpleBikeDTO.getBikeId());
        assertEquals(bikeName, simpleBikeDTO.getBikeName());
        assertEquals(bikeMake, simpleBikeDTO.getBikeMake());
        assertEquals(bikeModel, simpleBikeDTO.getBikeModel());
        assertEquals(serialNumber, simpleBikeDTO.getSerialNumber());
        assertEquals(yearOfProduction, simpleBikeDTO.getYearOfProduction());
        assertEquals(bikeFilesDTOList, simpleBikeDTO.getBikePhotosUrls());
    }

}