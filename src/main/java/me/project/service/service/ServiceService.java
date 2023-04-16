package me.project.service.service;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.dtos.response.services.ServiceDTO;
import me.project.repository.ServiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ServiceService implements IServiceService {
    private final ServiceRepository serviceRepository;

    private me.project.entitiy.Service findServiceById(UUID serviceId){
        return serviceRepository.findById(serviceId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service with given id " + serviceId + " not found")
        );
    }

    public ServiceDTO getServiceById(UUID serviceId) {
        return ServiceDTO.convertFromEntity(findServiceById(serviceId));
    }

    public PageResponse<ServiceDTO> getAllServicesDictionary(PageRequestDTO pageRequestDTO) {
        return new PageResponse<>(
                serviceRepository.findAll(pageRequestDTO.getRequest(me.project.entitiy.Service.class))
                        .map(ServiceDTO::convertFromEntity)
        );
    }

    public PageResponse<ServiceDTO> getAllServices(PageRequestDTO pageRequestDTO, String phrase) {

        if (phrase.isEmpty())
            serviceRepository.findAll(pageRequestDTO.getRequest(me.project.entitiy.Service.class))
                    .map(ServiceDTO::convertFromEntity);

        return new PageResponse<>(
                serviceRepository.findAllByServiceName(phrase, pageRequestDTO.getRequest(me.project.entitiy.Service.class))
                        .map(ServiceDTO::convertFromEntity)
        );
    }
}
