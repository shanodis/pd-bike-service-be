package me.project.service.bike;

import me.project.dtos.request.bike.BikeCreateRequestDTO;
import me.project.dtos.request.bike.BikeUpdateRequestDTO;
import me.project.dtos.response.bike.BikeResponseDTO;
import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.response.DictionaryResponseDTO;
import me.project.dtos.response.bikes.SimpleBikeDTO;
import me.project.dtos.response.page.PageResponse;

import java.util.List;
import java.util.UUID;

public interface IBikeService {
    BikeResponseDTO getBike(UUID bikeId);

    UUID createBike(BikeCreateRequestDTO request);

    List<SimpleBikeDTO> getBikesByUserAndPhrase(UUID userId, String phrase);

    PageResponse<DictionaryResponseDTO> getBikesDictionary(PageRequestDTO pageRequestDTO, UUID userId);

    void updateBike(UUID bikeId, BikeUpdateRequestDTO request);

    void deleteBike(UUID bikeId);
}
