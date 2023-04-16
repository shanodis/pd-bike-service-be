package me.project.service.bike;

import me.project.dtos.request.BikeCreateDTO;
import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.response.DictionaryResponseDTO;
import me.project.dtos.response.bikes.SimpleBikeDTO;
import me.project.dtos.response.page.PageResponse;

import java.util.List;
import java.util.UUID;

public interface IBikeService {
    UUID createBike(BikeCreateDTO request);

    List<SimpleBikeDTO> getBikesByUserAndPhrase(UUID userId, String phrase);

    PageResponse<DictionaryResponseDTO> getBikesDictionary(PageRequestDTO pageRequestDTO);
}
