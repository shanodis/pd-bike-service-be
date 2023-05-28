package me.project.controller;

import me.project.dtos.request.bike.BikeCreateRequestDTO;
import me.project.dtos.request.bike.BikeUpdateRequestDTO;
import me.project.dtos.response.bike.BikeFilesDTO;
import me.project.dtos.response.bike.BikeResponseDTO;
import me.project.service.bike.IBikeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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

    @GetMapping("{bikeId}/files")
    public List<BikeFilesDTO> getBikeFiles(@PathVariable UUID bikeId) {
        return bikeService.getBikeFilesUrls(bikeId);
    }

    @PostMapping
    public UUID createBike(@RequestBody BikeCreateRequestDTO request) {
        return bikeService.createBike(request);
    }

    @PostMapping(value = "{bikeId}/files/{orderNumber}", consumes = {"multipart/form-data"})
    public UUID addBikeFile(@PathVariable UUID bikeId, @PathVariable Integer orderNumber, @RequestParam("file") MultipartFile file) {
        return bikeService.addBikeFile(bikeId, orderNumber, file);
    }

    @PutMapping(value = "{bikeId}/files/{fileId}", consumes = {"multipart/form-data"})
    public void updateBikeFile(@PathVariable UUID bikeId, @PathVariable UUID fileId, @RequestParam("file") MultipartFile file) {
        bikeService.updateBikeFile(bikeId, fileId, file);
    }

    @DeleteMapping(value = "{bikeId}/files/{fileId}")
    public void deleteBikeFile(@PathVariable UUID bikeId, @PathVariable UUID fileId) {
        bikeService.deleteBikeFile(bikeId, fileId);
    }

    @PutMapping("{id}")
    public void updateBike(@PathVariable UUID id, @RequestBody BikeUpdateRequestDTO request) {
        bikeService.updateBike(id, request);
    }

    @DeleteMapping("{id}")
    public void deleteBike(@PathVariable UUID id) {
        bikeService.deleteBike(id);
    }
}
