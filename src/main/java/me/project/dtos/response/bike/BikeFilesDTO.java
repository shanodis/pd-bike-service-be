package me.project.dtos.response.bike;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BikeFilesDTO {

    private UUID fileId;
    private String fileUrl;

}
