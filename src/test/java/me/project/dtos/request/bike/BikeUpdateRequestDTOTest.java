package me.project.dtos.request.bike;

import me.project.entitiy.Bike;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("BikeUpdateRequestDTO")
class BikeUpdateRequestDTOTest {

    @Test
    @DisplayName("Should not update the bike if the input bike object is null")
    void convertToBikeDoesNotUpdateWhenInputIsNull() {
        BikeUpdateRequestDTO bikeUpdateRequestDTO = new BikeUpdateRequestDTO();
        Bike bike = null;

        assertThrows(NullPointerException.class, () -> bikeUpdateRequestDTO.convertToBike(bike));
    }

    @Test
    @DisplayName("Should update all fields of the bike with the provided values")
    void convertToBikeUpdatesAllFields() {// Create a mock Bike object
        Bike bike = new Bike();
        bike.setBikeName("New Bike Name");
        bike.setBikeMake("New Bike Make");
        bike.setBikeModel("New Bike Model");
        bike.setSerialNumber("New Serial Number");
        bike.setYearOfProduction(2022);

        // Create a BikeUpdateRequestDTO object
        BikeUpdateRequestDTO bikeUpdateRequestDTO = new BikeUpdateRequestDTO(
                "New Bike Name",
                "New Bike Make",
                "New Bike Model",
                "New Serial Number",
                2022
        );

        // Call the convertToBike method and pass the mock Bike object
        Bike updatedBike = bikeUpdateRequestDTO.convertToBike(bike);

        // Verify that all fields of the Bike object have been updated with the values from the BikeUpdateRequestDTO object
        assertEquals("New Bike Name", updatedBike.getBikeName());
        assertEquals("New Bike Make", updatedBike.getBikeMake());
        assertEquals("New Bike Model", updatedBike.getBikeModel());
        assertEquals("New Serial Number", updatedBike.getSerialNumber());
        assertEquals(2022, updatedBike.getYearOfProduction());
    }

}