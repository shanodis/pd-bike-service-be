package me.project.service.bike;

import me.project.dtos.request.BikeCreateDTO;

import java.util.UUID;

public interface IBikeService {
    UUID createBike(BikeCreateDTO request);
}
