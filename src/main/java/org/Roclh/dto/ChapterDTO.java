package org.Roclh.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChapterDTO {
    private Long id;

    @NotBlank(message = "Chapter name cannot be blank")
    private String name;

    private String parentLegion;
}