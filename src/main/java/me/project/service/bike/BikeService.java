package me.project.service.bike;

import me.project.auth.IUserService;
import me.project.dtos.request.BikeCreateDTO;
import me.project.entitiy.Bike;
import me.project.repository.BikeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
}
