package me.project.service.bike;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.bike.BikeCreateRequestDTO;
import me.project.dtos.request.bike.BikeUpdateRequestDTO;
import me.project.dtos.response.DictionaryResponseDTO;
import me.project.dtos.response.bike.BikeResponseDTO;
import me.project.dtos.response.bikes.SimpleBikeDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.entitiy.Bike;
import me.project.enums.SearchOperation;
import me.project.repository.BikeRepository;
import me.project.search.SearchCriteria;
import me.project.search.specificator.Specifications;
import me.project.service.user.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BikeService implements IBikeService {
    private final BikeRepository bikeRepository;
    private final IUserService userService;

    @Autowired
    public BikeService(@Lazy IUserService userService, BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
        this.userService = userService;
    }


    private String NOT_FOUND(UUID bikeId) {
        return "Bike with id" + bikeId + " doesn't exists in database";
    }

    public BikeResponseDTO getBike(UUID bikeId) {
        Bike bike = bikeRepository.findById(bikeId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(bikeId)
                ));

        return BikeResponseDTO.convertFromBike(bike);
    }

    public List<SimpleBikeDTO> getBikesByUserAndPhrase(UUID userId, String phrase) {

        Specifications<Bike> bikeSpecifications = new Specifications<>();

        if(userId != null)
            bikeSpecifications.and(new SearchCriteria("user", userService.getUser(userId), SearchOperation.EQUAL));

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

    public PageResponse<DictionaryResponseDTO> getBikesDictionary(PageRequestDTO pageRequestDTO, UUID userId) {

        if (userId != null) {
            Specifications<Bike> bikeSpecifications = new Specifications<Bike>()
                    .and(new SearchCriteria("userId", userId, SearchOperation.EQUAL_JOIN_USER));

            return new PageResponse<>(
                    bikeRepository.findAll(
                            bikeSpecifications,
                            pageRequestDTO.getRequest(Bike.class)
                    ).map(bike -> new DictionaryResponseDTO(bike.getBikeId(), bike.getBikeName()))
            );

        }

        return new PageResponse<>(
                bikeRepository.findAll(pageRequestDTO.getRequest(Bike.class))
                        .map(bike -> new DictionaryResponseDTO(bike.getBikeId(), bike.getBikeName()))
        );
    }

    public UUID createBike(BikeCreateRequestDTO request) {
        Bike entity = request.convertToBike(userService.getUser(request.getUserId()));
        bikeRepository.save(entity);

        return entity.getBikeId();
    }

    public void updateBike(UUID bikeId, BikeUpdateRequestDTO request) {
        Bike bike = bikeRepository.findById(bikeId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(bikeId)
                ));

        bike = request.convertToBike(bike);

        bikeRepository.save(bike);
    }

    public void deleteBike(UUID bikeId) {
        bikeRepository.findById(bikeId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(bikeId))
        );

        bikeRepository.deleteById(bikeId);
    }


    public Bike getBike(String bikeName) {
        return bikeRepository.findBikeByBikeNameContaining(bikeName).orElse(null);
    }

}
