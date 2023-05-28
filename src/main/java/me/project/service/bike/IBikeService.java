package me.project.service.bike;

import me.project.dtos.request.bike.BikeCreateRequestDTO;
import me.project.dtos.request.bike.BikeUpdateRequestDTO;
import me.project.dtos.response.bike.BikeFilesDTO;
import me.project.dtos.response.bike.BikeResponseDTO;
import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.response.DictionaryResponseDTO;
import me.project.dtos.response.bikes.SimpleBikeDTO;
import me.project.dtos.response.page.PageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IBikeService {
    BikeResponseDTO getBike(UUID bikeId);

    List<SimpleBikeDTO> getBikesByUserAndPhrase(UUID userId, String phrase);

    List<BikeFilesDTO> getBikeFilesUrls(UUID bikeId);

    PageResponse<DictionaryResponseDTO> getBikesDictionary(PageRequestDTO pageRequestDTO, UUID userId);

    UUID createBike(BikeCreateRequestDTO request);

    UUID addBikeFile(UUID bikeId, Integer orderNumber, MultipartFile file);

    void updateBike(UUID bikeId, BikeUpdateRequestDTO request);

    void deleteBike(UUID bikeId);

    void deleteBikeFile(UUID bikeId, UUID fileId);

    void updateBikeFile(UUID bikeId, UUID fileId, MultipartFile file);
}
