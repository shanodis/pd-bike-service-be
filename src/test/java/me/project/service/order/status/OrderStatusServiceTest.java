package me.project.service.order.status;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.response.DictionaryResponseDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.entitiy.OrderStatus;
import me.project.repository.OrderStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStatusServiceTest {

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @InjectMocks
    private OrderStatusService orderStatusService;


    @Test
    @DisplayName("Should return an empty page of statuses dictionary when no statuses are available")
    void getAllStatusesDictionaryWhenNoStatusesAreAvailable() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "orderStatusName");
        when(orderStatusRepository.findAll(pageRequestDTO.getRequest(OrderStatus.class))).thenReturn(Page.empty());

        PageResponse<DictionaryResponseDTO> pageResponse = orderStatusService.getAllStatusesDictionary(pageRequestDTO);

        assertEquals(0, pageResponse.getContent().size());
        assertEquals(1, pageResponse.getCurrentPage());
        assertEquals(1, pageResponse.getTotalPages());
        verify(orderStatusRepository, times(1)).findAll(pageRequestDTO.getRequest(OrderStatus.class));
    }

    @Test
    @DisplayName("Should return a page of statuses dictionary when valid page request is provided")
    void getAllStatusesDictionaryWhenValidPageRequestIsProvided() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "orderStatusName");
        OrderStatus orderStatus = new OrderStatus(UUID.randomUUID(), "Test Status");
        when(orderStatusRepository.findAll(pageRequestDTO.getRequest(OrderStatus.class))).thenReturn(
                new PageImpl<>(Collections.singletonList(orderStatus))
        );

        PageResponse<DictionaryResponseDTO> result = orderStatusService.getAllStatusesDictionary(pageRequestDTO);

        assertEquals(1, result.getContent().size());
        assertEquals(orderStatus.getOrderStatusId(), result.getContent().get(0).getId());
        assertEquals(orderStatus.getOrderStatusName(), result.getContent().get(0).getName());
        verify(orderStatusRepository, times(1)).findAll(pageRequestDTO.getRequest(OrderStatus.class));
    }

    @Test
    @DisplayName("Should throw a ResponseStatusException when the orderStatusId is not found")
    void getStatusByIdWhenOrderStatusIdNotFoundThenThrowResponseStatusException() {
        UUID orderStatusId = UUID.randomUUID();
        when(orderStatusRepository.findById(orderStatusId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> orderStatusService.getStatusById(orderStatusId));
        verify(orderStatusRepository, times(1)).findById(orderStatusId);
    }

    @Test
    @DisplayName("Should return the order status when the orderStatusId is valid")
    void getStatusByIdWhenOrderStatusIdIsValid() {
        UUID orderStatusId = UUID.randomUUID();
        OrderStatus orderStatus = new OrderStatus(orderStatusId, "Test Status");
        when(orderStatusRepository.findById(orderStatusId)).thenReturn(Optional.of(orderStatus));

        OrderStatus result = orderStatusService.getStatusById(orderStatusId);

        assertEquals(orderStatus, result);
        verify(orderStatusRepository, times(1)).findById(orderStatusId);
    }
}