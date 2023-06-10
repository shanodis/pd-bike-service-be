package me.project.service.bike;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.bike.BikeCreateRequestDTO;
import me.project.dtos.request.bike.BikeUpdateRequestDTO;
import me.project.dtos.response.DictionaryResponseDTO;
import me.project.dtos.response.bike.BikeFilesDTO;
import me.project.dtos.response.bike.BikeResponseDTO;
import me.project.dtos.response.bikes.SimpleBikeDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.entitiy.Bike;
import me.project.entitiy.BikeFile;
import me.project.enums.SearchOperation;
import me.project.repository.*;
import me.project.search.SearchCriteria;
import me.project.search.specificator.Specifications;
import me.project.service.files.IFileService;
import me.project.service.user.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BikeService implements IBikeService {
    private final IFileService fileService;
    private final BikeRepository bikeRepository;
    private final OrderServiceRepository orderServiceRepository;
    private final OrderPartRepository orderPartRepository;
    private final OrderRepository orderRepository;
    private final IUserService userService;
    private final BikeFileRepository bikeFileRepository;

    @Autowired
    public BikeService(IFileService fileService,
                       @Lazy IUserService userService,
                       BikeRepository bikeRepository,
                       OrderServiceRepository orderServiceRepository,
                       OrderPartRepository orderPartRepository,
                       OrderRepository orderRepository,
                       BikeFileRepository bikeFileRepository) {
        this.fileService = fileService;
        this.bikeRepository = bikeRepository;
        this.userService = userService;
        this.orderServiceRepository = orderServiceRepository;
        this.orderPartRepository = orderPartRepository;
        this.orderRepository = orderRepository;
        this.bikeFileRepository = bikeFileRepository;
    }


    private String NOT_FOUND(UUID bikeId) {
        return "Bike with id" + bikeId + " doesn't exists in database";
    }

    public BikeResponseDTO getBike(UUID bikeId) {
        Bike bike = bikeRepository.findById(bikeId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(bikeId))
        );

        return BikeResponseDTO.convertFromBike(bike);
    }

    public List<SimpleBikeDTO> getBikesByUserAndPhrase(UUID userId, String phrase) {

        Specifications<Bike> bikeSpecifications = new Specifications<>();

        if (userId != null)
            bikeSpecifications.and(new SearchCriteria("user", userService.getUser(userId), SearchOperation.EQUAL));

        if (phrase != null)
            bikeSpecifications
                    .or(new SearchCriteria("bikeName", phrase.trim(), SearchOperation.MATCH))
                    .or(new SearchCriteria("bikeMake", phrase.trim(), SearchOperation.MATCH))
                    .or(new SearchCriteria("bikeModel", phrase.trim(), SearchOperation.MATCH))
                    .or(new SearchCriteria("serialNumber", phrase.trim(), SearchOperation.MATCH));

        return bikeRepository.findAll(bikeSpecifications)
                .stream()
                .map(bike -> SimpleBikeDTO.convertFromEntity(
                                bike,
                                fileService.getFilesUrls(bike.getBikeFiles()
                                        .stream()
                                        .map(bikeFile -> bikeFile.getFile())
                                        .collect(Collectors.toList()))
                        )
                )
                .collect(Collectors.toList());
    }

    public List<BikeFilesDTO> getBikeFilesUrls(UUID bikeId) {

        Bike bike = bikeRepository.findById(bikeId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(bikeId))
        );

        List<BikeFile> bikeFiles = bike.getBikeFiles();

        bikeFiles
                .sort((o1, o2) -> o1.getOrderNumber() > o2.getOrderNumber() ? 1 : -1);

        return fileService.getFilesUrls(bikeFiles
                .stream()
                .map(BikeFile::getFile)
                .collect(Collectors.toList()));
    }

    public PageResponse<DictionaryResponseDTO> getBikesDictionary(PageRequestDTO pageRequestDTO, UUID userId) {

        if (userId != null) {
            Specifications<Bike> bikeSpecifications = new Specifications<Bike>()
                    .and(new SearchCriteria("userId", userId, SearchOperation.EQUAL_JOIN_USER));

            Page<Bike> bikes = bikeRepository.findAll(
                    bikeSpecifications,
                    pageRequestDTO.getRequest(Bike.class)
            );

            if (bikes != null) {
                return new PageResponse<>(
                        bikes.map(bike -> new DictionaryResponseDTO(bike.getBikeId(), bike.getBikeName()))
                );
            }

            return new PageResponse<>(Page.empty());
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

    public UUID addBikeFile(UUID bikeId, Integer orderNumber, MultipartFile file) {

        Bike bike = bikeRepository.findById(bikeId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(bikeId))
        );


        UUID uploadedFileId = fileService.uploadFile(file);

        if (bike.getBikeFiles().stream().anyMatch(bikeFile -> bikeFile.getFile().getFileId().equals(uploadedFileId)))
            return uploadedFileId;

        bikeFileRepository.save(new BikeFile(
                bike,
                fileService.getFileById(uploadedFileId),
                orderNumber));

        return uploadedFileId;
    }


    public void updateBikeFile(UUID bikeId, UUID fileId, MultipartFile file) {

        Bike bike = bikeRepository.findById(bikeId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(bikeId))
        );

        UUID uploadedFileId = fileService.uploadFile(file);

        Optional<BikeFile> optionalBikeFile = bike.getBikeFiles()
                .stream()
                .filter(bikeFile -> bikeFile.getFile().getFileId().equals(fileId))
                .findFirst();

        BikeFile bikeFileToUpdate;

        if (optionalBikeFile.isPresent())
            bikeFileToUpdate = optionalBikeFile.get();
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Bike file with file of id %s not found", fileId));

        bikeFileToUpdate.setFile(null);

        bikeFileRepository.save(bikeFileToUpdate);

        fileService.deleteFile(fileId);

        bikeFileToUpdate.setFile(fileService.getFileById(uploadedFileId));

        bikeFileRepository.save(bikeFileToUpdate);

    }

    public void deleteBikeFile(UUID bikeId, UUID fileId) {

        Bike bike = bikeRepository.findById(bikeId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(bikeId))
        );

        Optional<BikeFile> optionalBikeFile = bike.getBikeFiles()
                .stream()
                .filter(bikeFile -> bikeFile.getFile().getFileId().equals(fileId))
                .findFirst();

        BikeFile bikeFileToDelete;

        if (optionalBikeFile.isPresent())
            bikeFileToDelete = optionalBikeFile.get();
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Bike file with file of id %s not found", fileId));

        bikeFileToDelete.setFile(null);

        bikeFileRepository.save(bikeFileToDelete);

        fileService.deleteFile(fileId);

        bikeFileRepository.delete(bikeFileToDelete);
    }

    public void updateBike(UUID bikeId, BikeUpdateRequestDTO request) {
        Bike bike = bikeRepository.findById(bikeId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(bikeId)
                ));

        bike = request.convertToBike(bike);

        bikeRepository.save(bike);
    }

    @Transactional
    public void deleteBike(UUID bikeId) {

        Bike bike = bikeRepository.findById(bikeId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(bikeId))
        );

        bike.setUser(null);

        bikeRepository.save(bike);

        bike.getOrders()
                .forEach(order -> {
                    orderServiceRepository.deleteAll(order.getOrderServices());
                    orderPartRepository.deleteAll(order.getOrderParts());
                });


        orderRepository.deleteAll(bike.getOrders());

        bikeRepository.deleteById(bikeId);
    }
}
