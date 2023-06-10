package me.project.dtos.request.bike;

import me.project.entitiy.Bike;
import me.project.entitiy.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BikeCreateRequestDTO")
class BikeCreateRequestDTOTest {


    @Test
    @DisplayName("Should convert BikeCreateRequestDTO to Bike with only required fields set")
    void convertToBikeWithOnlyRequiredFieldsSet() {
        UUID userId = UUID.randomUUID();
        String bikeName = "Test Bike";
        String bikeMake = "Test Make";
        String bikeModel = "Test Model";
        User user = new User();
        user.setUserId(userId);

        BikeCreateRequestDTO bikeCreateRequestDTO = new BikeCreateRequestDTO();
        bikeCreateRequestDTO.setUserId(userId);
        bikeCreateRequestDTO.setBikeName(bikeName);
        bikeCreateRequestDTO.setBikeMake(bikeMake);
        bikeCreateRequestDTO.setBikeModel(bikeModel);

        Bike bike = bikeCreateRequestDTO.convertToBike(user);

        assertNotNull(bike);
        assertEquals(user, bike.getUser());
        assertEquals(bikeName, bike.getBikeName());
        assertEquals(bikeMake, bike.getBikeMake());
        assertEquals(bikeModel, bike.getBikeModel());
        assertNull(bike.getSerialNumber());
        assertNull(bike.getYearOfProduction());
    }

    @Test
    @DisplayName("Should convert BikeCreateRequestDTO to Bike with all fields set")
    void convertToBikeWithAllFieldsSet() {
        UUID userId = UUID.randomUUID();
        String bikeName = "Test Bike";
        String bikeMake = "Test Make";
        String bikeModel = "Test Model";
        String serialNumber = "123456789";
        Integer yearOfProduction = 2021;

        User user = new User();
        user.setUserId(userId);

        BikeCreateRequestDTO bikeCreateRequestDTO = new BikeCreateRequestDTO();
        bikeCreateRequestDTO.setUserId(userId);
        bikeCreateRequestDTO.setBikeName(bikeName);
        bikeCreateRequestDTO.setBikeMake(bikeMake);
        bikeCreateRequestDTO.setBikeModel(bikeModel);
        bikeCreateRequestDTO.setSerialNumber(serialNumber);
        bikeCreateRequestDTO.setYearOfProduction(yearOfProduction);

        Bike bike = bikeCreateRequestDTO.convertToBike(user);

        assertNotNull(bike);
        assertEquals(user, bike.getUser());
        assertEquals(bikeName, bike.getBikeName());
        assertEquals(bikeMake, bike.getBikeMake());
        assertEquals(bikeModel, bike.getBikeModel());
        assertEquals(serialNumber, bike.getSerialNumber());
        assertEquals(yearOfProduction, bike.getYearOfProduction());
    }

}