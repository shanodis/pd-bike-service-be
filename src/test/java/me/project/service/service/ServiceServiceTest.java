package me.project.service.service;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.service.CreateServiceDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.dtos.response.services.ServiceDTO;
import me.project.entitiy.Service;
import me.project.repository.ServiceRepository;
import me.project.search.specificator.Specifications;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServiceService serviceService;

    @Test
    @DisplayName("Should return all services without filtering when phrase is null")
    void getAllServicesDictionaryWhenPhraseIsNull() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "serviceName");
        String phrase = null;

        List<Service> services = new ArrayList<>();
        services.add(new Service(UUID.randomUUID(), "Service 1", new BigDecimal("10.00"), new ArrayList<>()));
        services.add(new Service(UUID.randomUUID(), "Service 2", new BigDecimal("20.00"), new ArrayList<>()));
        services.add(new Service(UUID.randomUUID(), "Service 3", new BigDecimal("30.00"), new ArrayList<>()));

        Page<Service> page = new PageImpl<>(services);

        when(serviceRepository.findAll(pageRequestDTO.getRequest(Service.class))).thenReturn(page);

        PageResponse<ServiceDTO> result = serviceService.getAllServicesDictionary(pageRequestDTO, phrase);

        assertEquals(1, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(3, result.getContent().size());
        assertEquals("Service 1", result.getContent().get(0).getServiceName());
        assertEquals(new BigDecimal("10.00"), result.getContent().get(0).getServicePrice());
        assertEquals("Service 2", result.getContent().get(1).getServiceName());
        assertEquals(new BigDecimal("20.00"), result.getContent().get(1).getServicePrice());
        assertEquals("Service 3", result.getContent().get(2).getServiceName());
        assertEquals(new BigDecimal("30.00"), result.getContent().get(2).getServicePrice());

        verify(serviceRepository, times(1)).findAll(pageRequestDTO.getRequest(Service.class));
    }

    @Test
    @DisplayName("Should throw a ResponseStatusException when the serviceId is not found")
    void getServiceByIdWhenServiceIdNotFoundThenThrowException() {
        UUID serviceId = UUID.randomUUID();

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> serviceService.getServiceById(serviceId));

        verify(serviceRepository, times(1)).findById(serviceId);
    }

    @Test
    @DisplayName("Should return all services when phrase is null")
    void testGetAllServicesWhenPhraseIsNull() {
        // create test data
        Integer page = 1;
        Integer pageLimit = 10;
        String sortDir = "asc";
        String sortBy = "serviceName";
        PageRequestDTO pageRequestDTO = new PageRequestDTO(page, pageLimit, sortDir, sortBy);
        String phrase = null;

        List<me.project.entitiy.Service> serviceList = new ArrayList<>();
        serviceList.add(new me.project.entitiy.Service(UUID.randomUUID(), "Test Service 1", new BigDecimal("10.00"), new ArrayList<>()));
        serviceList.add(new me.project.entitiy.Service(UUID.randomUUID(), "Test Service 2", new BigDecimal("20.00"), new ArrayList<>()));
        serviceList.add(new me.project.entitiy.Service(UUID.randomUUID(), "Another Test Service", new BigDecimal("15.00"), new ArrayList<>()));

        Page<me.project.entitiy.Service> servicePage = new PageImpl<>(serviceList);

        when(serviceRepository.findAll(any(Pageable.class))).thenReturn(servicePage);

        // call the method being tested
        PageResponse<ServiceDTO> result = serviceService.getAllServices(pageRequestDTO, phrase);

        // assert that the result contains the expected services
        assertEquals(serviceList.size(), result.getContent().size());
        for (int i = 0; i < serviceList.size(); i++) {
            ServiceDTO serviceDTO = result.getContent().get(i);
            me.project.entitiy.Service service = serviceList.get(i);
            assertEquals(service.getServiceId(), serviceDTO.getServiceId());
            assertEquals(service.getServiceName(), serviceDTO.getServiceName());
            assertEquals(service.getServicePrice(), serviceDTO.getServicePrice());
        }

        // assert that the repository method was called with the expected arguments
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(serviceRepository, times(1)).findAll(pageableCaptor.capture());
    }

    @Test
    @DisplayName("Should return filtered services when phrase is not null")
    void testGetAllServicesWhenPhraseIsNotNull() {
        // create test data
        Integer page = 1;
        Integer pageLimit = 10;
        String sortDir = "asc";
        String sortBy = "serviceName";
        PageRequestDTO pageRequestDTO = new PageRequestDTO(page, pageLimit, sortDir, sortBy);
        String phrase = "test";

        List<me.project.entitiy.Service> serviceList = new ArrayList<>();
        serviceList.add(new me.project.entitiy.Service(UUID.randomUUID(), "Test Service 1", new BigDecimal("10.00"), new ArrayList<>()));
        serviceList.add(new me.project.entitiy.Service(UUID.randomUUID(), "Test Service 2", new BigDecimal("20.00"), new ArrayList<>()));

        Page<me.project.entitiy.Service> servicePage = new PageImpl<>(serviceList);

        when(serviceRepository.findAllByServiceName(eq(phrase), any(Pageable.class))).thenReturn(servicePage);

        // call the method being tested
        PageResponse<ServiceDTO> result = serviceService.getAllServices(pageRequestDTO, phrase);

        // assert that the result contains the expected services
        assertEquals(serviceList.size(), result.getContent().size());
        for (int i = 0; i < serviceList.size(); i++) {
            ServiceDTO serviceDTO = result.getContent().get(i);
            me.project.entitiy.Service service = serviceList.get(i);
            assertEquals(service.getServiceId(), serviceDTO.getServiceId());
            assertEquals(service.getServiceName(), serviceDTO.getServiceName());
            assertEquals(service.getServicePrice(), serviceDTO.getServicePrice());
        }

        // assert that the repository method was called with the expected arguments
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(serviceRepository, times(1)).findAllByServiceName(eq(phrase), pageableCaptor.capture());
    }

    @Test
    @DisplayName("Should return the ServiceDTO when the serviceId is valid")
    void getServiceByIdWhenServiceIdIsValid() {
        UUID serviceId = UUID.randomUUID();
        Service service = new Service(serviceId, "Test Service", new BigDecimal("10.00"), new ArrayList<>());
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));

        ServiceDTO result = serviceService.getServiceById(serviceId);

        assertNotNull(result);
        assertEquals(serviceId, result.getServiceId());
        assertEquals("Test Service", result.getServiceName());
        assertEquals(new BigDecimal("10.00"), result.getServicePrice());

        verify(serviceRepository, times(1)).findById(serviceId);
    }

    @Test
    @DisplayName("Should return services matching the phrase when phrase is not null")
    void getAllServicesDictionaryWhenPhraseIsNotNull() {
        String phrase = "test";
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "serviceName");
        ArrayList<Service> services = new ArrayList<>();
        services.add(new Service(UUID.randomUUID(), "test1", BigDecimal.valueOf(10), new ArrayList<>()));
        services.add(new Service(UUID.randomUUID(), "test2", BigDecimal.valueOf(20), new ArrayList<>()));
        when(serviceRepository.findAll(any(Specifications.class), any(Pageable.class))).thenReturn(new PageImpl<>(services));

        PageResponse<ServiceDTO> result = serviceService.getAllServicesDictionary(pageRequestDTO, phrase);

        assertNotNull(result);
        assertNotNull(result.getContent());
        verify(serviceRepository, times(1)).findAll(any(Specifications.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should throw a conflict exception when the service with the same name and price already exists")
    void createServiceWhenServiceWithSameNameAndPriceExistsThenThrowConflictException() {
        CreateServiceDTO createServiceDTO = new CreateServiceDTO("Service1", new BigDecimal("10.00"));
        Service existingService = new Service(
                UUID.randomUUID(),
                "Service1",
                new BigDecimal("10.00"),
                new ArrayList<>()
        );
        Optional<Service> optionalExistingService = Optional.of(existingService);

        when(serviceRepository.findByServiceName(createServiceDTO.getServiceName())).thenReturn(optionalExistingService);

        assertThrows(ResponseStatusException.class, () -> serviceService.createService(createServiceDTO));

        verify(serviceRepository, never()).save(any(Service.class));
    }

    @Test
    @DisplayName("Should create a new service when the service name and price are unique")
    void createServiceWhenServiceNameAndPriceAreUnique() {
        CreateServiceDTO createServiceDTO = new CreateServiceDTO("Test Service", new BigDecimal("10.00"));
        Service service = new Service("Test Service", new BigDecimal("10.00"));
        service.setServiceId(UUID.randomUUID());
        when(serviceRepository.findByServiceName(createServiceDTO.getServiceName())).thenReturn(Optional.empty());
        when(serviceRepository.save(any(Service.class))).thenReturn(service);

        UUID serviceId = serviceService.createService(createServiceDTO);

        verify(serviceRepository, times(1)).findByServiceName(createServiceDTO.getServiceName());
        verify(serviceRepository, times(1)).save(any(Service.class));

        assertNotNull(serviceId);
    }
}