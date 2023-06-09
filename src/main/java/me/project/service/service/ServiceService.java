package me.project.service.service;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.service.CreateServiceDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.dtos.response.services.ServiceDTO;
import me.project.enums.SearchOperation;
import me.project.repository.ServiceRepository;
import me.project.search.SearchCriteria;
import me.project.search.specificator.Specifications;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ServiceService implements IServiceService {
    private final ServiceRepository serviceRepository;

    private me.project.entitiy.Service findServiceById(UUID serviceId) {
        return serviceRepository.findById(serviceId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service with given id " + serviceId + " not found")
        );
    }

    public ServiceDTO getServiceById(UUID serviceId) {
        return ServiceDTO.convertFromEntity(findServiceById(serviceId));
    }

    public PageResponse<ServiceDTO> getAllServicesDictionary(PageRequestDTO pageRequestDTO, String phrase) {

        Specifications<me.project.entitiy.Service> serviceSpecifications;

        if (phrase != null) {
            serviceSpecifications = new Specifications<me.project.entitiy.Service>()
                    .or(new SearchCriteria("serviceName", phrase, SearchOperation.MATCH));

            return new PageResponse<>(
                    serviceRepository.findAll(serviceSpecifications, pageRequestDTO.getRequest(me.project.entitiy.Service.class))
                            .map(ServiceDTO::convertFromEntity)
            );
        }

        return new PageResponse<>(
                serviceRepository.findAll(pageRequestDTO.getRequest(me.project.entitiy.Service.class))
                        .map(ServiceDTO::convertFromEntity)
        );
    }

    public PageResponse<ServiceDTO> getAllServices(PageRequestDTO pageRequestDTO, String phrase) {

        if (phrase == null)
            return new PageResponse<>(
                    serviceRepository.findAll(pageRequestDTO.getRequest(me.project.entitiy.Service.class))
                            .map(ServiceDTO::convertFromEntity)
            );

        return new PageResponse<>(
                serviceRepository.findAllByServiceName(phrase, pageRequestDTO.getRequest(me.project.entitiy.Service.class))
                        .map(ServiceDTO::convertFromEntity)
        );
    }

    public UUID createService(CreateServiceDTO createServiceDTO) {

        Optional<me.project.entitiy.Service> service = serviceRepository.findByServiceName(createServiceDTO.getServiceName());

        if (service.isPresent())
            if (service.get().getServicePrice().equals(createServiceDTO.getServicePrice()))
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        String.format("Service %s already exists with %.2f price",
                                createServiceDTO.getServiceName(),
                                createServiceDTO.getServicePrice()
                        )
                );

        return serviceRepository.save(new me.project.entitiy.Service(
                createServiceDTO.getServiceName(),
                createServiceDTO.getServicePrice()
        )).getServiceId();

    }
}
