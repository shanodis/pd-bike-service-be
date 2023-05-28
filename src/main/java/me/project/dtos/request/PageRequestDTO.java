package me.project.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

    @NotNull
    private Integer PageNumber;

    @NotNull
    private Integer PageSize;

    @NotNull
    private String SortDirection;

    @NotNull
    private String SortByPropertyName;

    public <T> PageRequest getRequest(Class<T> cls) {
        if(Arrays.stream(cls.getDeclaredFields()).noneMatch(field -> field.getName().equals(SortByPropertyName)))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Sorting Field:" + SortByPropertyName + " doesn't exist on this entity");

        if(!SortDirection.equals("ASC") && !SortDirection.equals("DESC"))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Sort Direction must be ASC or DESC");

        if(PageNumber < 1)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Page Number cannot be less than 1");

        if(PageSize < -1 || PageSize == 0)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Page Size cannot be less than -1 or equal 0");

        if(PageSize == -1)
            return PageRequest.of(PageNumber - 1, Integer.MAX_VALUE, Sort.Direction.valueOf(SortDirection) , SortByPropertyName);

        return PageRequest.of(PageNumber - 1, PageSize, Sort.Direction.valueOf(SortDirection) , SortByPropertyName);
    }

}
