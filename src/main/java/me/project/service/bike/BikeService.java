package me.project.service.bike;

import me.project.dtos.request.BikeCreateDTO;
import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.response.DictionaryResponseDTO;
import me.project.dtos.response.bikes.SimpleBikeDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.entitiy.Bike;
import me.project.enums.SearchOperation;
import me.project.repository.BikeRepository;
import me.project.search.SearchCriteria;
import me.project.search.specificator.Specifications;
import me.project.service.user.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BikeService implements IBikeService {
    private final BikeRepository bikeRepository;
    private final IUserService userService;

    public UUID createBike(BikeCreateDTO request) {
        Bike entity = request.convertToBike(userService.getUser(request.getUserId()));
        bikeRepository.save(entity);

        return entity.getBikeId();
    }

    public List<SimpleBikeDTO> getBikesByUserAndPhrase(UUID userId, String phrase) {

        Specifications<Bike> bikeSpecifications = new Specifications<Bike>()
                .and(new SearchCriteria("user", userService.getUser(userId), SearchOperation.EQUAL));

        if (phrase != null)
            bikeSpecifications
                    .or(new SearchCriteria("bikeName", phrase.trim(), SearchOperation.MATCH))
                    .or(new SearchCriteria("bikeMake", phrase.trim(), SearchOperation.MATCH))
                    .or(new SearchCriteria("bikeModel", phrase.trim(), SearchOperation.MATCH))
                    .or(new SearchCriteria("serialNumber", phrase.trim(), SearchOperation.MATCH));

        return bikeRepository.findAll(bikeSpecifications)
                .stream()
                .map(SimpleBikeDTO::convertFromEntity)
                .collect(Collectors.toList());
    }

    public PageResponse<DictionaryResponseDTO> getBikesDictionary(PageRequestDTO pageRequestDTO) {
        return new PageResponse<>(
                bikeRepository.findAll(pageRequestDTO.getRequest(Bike.class))
                        .map(bike -> new DictionaryResponseDTO(bike.getBikeId(), bike.getBikeName()))
        );
    }
}
