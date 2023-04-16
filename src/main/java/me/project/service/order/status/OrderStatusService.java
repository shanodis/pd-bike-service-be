package me.project.service.order.status;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.response.DictionaryResponseDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.entitiy.OrderStatus;
import me.project.repository.OrderStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderStatusService implements IOrderStatusService {
    private final OrderStatusRepository orderStatusRepository;

    public PageResponse<DictionaryResponseDTO> getAllStatusesDictionary(PageRequestDTO pageRequestDTO) {
        return new PageResponse<>(
                orderStatusRepository.findAll(pageRequestDTO.getRequest(OrderStatus.class))
                        .map(orderStatus -> new DictionaryResponseDTO(orderStatus.getOrderStatusId(), orderStatus.getOrderStatusName()))
        );
    }
}
