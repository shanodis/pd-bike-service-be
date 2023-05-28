package me.project.controller;

import me.project.dtos.request.bike.BikeCreateRequestDTO;
import me.project.dtos.request.bike.BikeUpdateRequestDTO;
import me.project.dtos.response.bike.BikeResponseDTO;
import me.project.service.bike.IBikeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/bikes")
@AllArgsConstructor
public class BikeController {
    private final IBikeService bikeService;

    @GetMapping("{id}")
    public BikeResponseDTO getBike(@PathVariable UUID id) {
        return bikeService.getBike(id);
    }

    @PostMapping
    public UUID createBike(@RequestBody BikeCreateRequestDTO request) {
        return bikeService.createBike(request);
    }

    @PutMapping("{id}")
    public void updateBike(@PathVariable UUID id, @RequestBody BikeUpdateRequestDTO request){
        bikeService.updateBike(id, request);
    }

    @DeleteMapping("{id}")
    public void deleteBike(@PathVariable UUID id) {
        bikeService.deleteBike(id);
    }
}
