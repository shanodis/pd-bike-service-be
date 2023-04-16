package me.project.controller;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.dtos.response.services.ServiceDTO;
import me.project.service.service.IServiceService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/services")
@AllArgsConstructor
public class ServiceController {
    private final IServiceService serviceService;

    @GetMapping("{serviceId}")
    public ServiceDTO getService(@PathVariable UUID serviceId){
        return serviceService.getServiceById(serviceId);
    }

    @GetMapping
    public PageResponse<ServiceDTO> getServices(@RequestParam Integer pageNumber, @RequestParam Integer pageSize,
                                                @RequestParam String sortDir, @RequestParam String sortBy,
                                                @RequestParam(required = false) String phrase) {

        return serviceService.getAllServices(new PageRequestDTO(pageNumber, pageSize, sortDir, sortBy), phrase);
    }

}
