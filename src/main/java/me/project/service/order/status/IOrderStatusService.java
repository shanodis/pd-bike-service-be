package me.project.service.order.status;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.response.DictionaryResponseDTO;
import me.project.dtos.response.page.PageResponse;

public interface IOrderStatusService {
    PageResponse<DictionaryResponseDTO> getAllStatusesDictionary(PageRequestDTO pageRequestDTO);
}
