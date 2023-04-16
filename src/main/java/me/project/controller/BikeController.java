package me.project.controller;

import me.project.dtos.request.BikeCreateDTO;
import me.project.service.bike.IBikeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("bikes")
@AllArgsConstructor
public class BikeController {
    private final IBikeService bikeService;

    @PostMapping
    public UUID createBike(@RequestBody BikeCreateDTO request) {
        return bikeService.createBike(request);
    }

}
