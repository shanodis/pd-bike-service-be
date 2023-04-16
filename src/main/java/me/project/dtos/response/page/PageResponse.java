package me.project.dtos.response.page;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageResponse<T> {
    private final Integer currentPage;
    private final Integer totalPages;
    private final List<T> content;

    public PageResponse(Page<T> page) {
        content = page.getContent();
        currentPage = page.getNumber() + 1;
        totalPages = page.getTotalPages();
    }
}
