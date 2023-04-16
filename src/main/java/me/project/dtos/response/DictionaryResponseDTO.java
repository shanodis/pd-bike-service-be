package me.project.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DictionaryResponseDTO {
    private final UUID id;
    private final String name;
}
