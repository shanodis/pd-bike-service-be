package me.project.dtos.response.bike;

import me.project.entitiy.Bike;
import me.project.entitiy.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BikeResponseDTO")
class BikeResponseDTOTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("Should convert a Bike object to BikeResponseDTO object with null optional fields")
    void convertFromBikeWithNullOptionalFields() {
        Bike bike = new Bike(user, "BikeName", "BikeMake", "BikeModel", "SerialNumber", null);
        BikeResponseDTO bikeResponseDTO = BikeResponseDTO.convertFromBike(bike);

        assertNotNull(bikeResponseDTO);
        assertEquals(bike.getBikeName(), bikeResponseDTO.getBikeName());
        assertEquals(bike.getBikeMake(), bikeResponseDTO.getBikeMake());
        assertEquals(bike.getBikeModel(), bikeResponseDTO.getBikeModel());
        assertEquals(bike.getSerialNumber(), bikeResponseDTO.getSerialNumber());
        assertNull(bikeResponseDTO.getYearOfProduction());
    }

    @Test
    @DisplayName("Should convert a Bike object to BikeResponseDTO object with all fields")
    void convertFromBikeWithAllFields() {
        Bike bike = new Bike(user, "BikeName", "BikeMake", "BikeModel", "SerialNumber", 2021);
        BikeResponseDTO bikeResponseDTO = BikeResponseDTO.convertFromBike(bike);

        assertNotNull(bikeResponseDTO);
        assertEquals(bike.getBikeName(), bikeResponseDTO.getBikeName());
        assertEquals(bike.getBikeMake(), bikeResponseDTO.getBikeMake());
        assertEquals(bike.getBikeModel(), bikeResponseDTO.getBikeModel());
        assertEquals(bike.getSerialNumber(), bikeResponseDTO.getSerialNumber());
        assertEquals(bike.getYearOfProduction(), bikeResponseDTO.getYearOfProduction());
    }

}