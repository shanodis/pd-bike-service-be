package me.project.service.service;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.service.CreateServiceDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.dtos.response.services.ServiceDTO;

import java.util.UUID;

public interface IServiceService {
    ServiceDTO getServiceById(UUID serviceId);

    PageResponse<ServiceDTO> getAllServicesDictionary(PageRequestDTO pageRequestDTO, String phrase);

    PageResponse<ServiceDTO> getAllServices(PageRequestDTO pageRequestDTO, String phrase);

    UUID createService(CreateServiceDTO createServiceDTO);

}
