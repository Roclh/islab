package org.Roclh.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChapterDTO {
    private Long id;

    @NotBlank(message = "Chapter name cannot be blank")
    private String name;

    private String parentLegion;
    @DecimalMin(value = "0", inclusive = false, message = "Count must be greater than 0")
    @DecimalMax(value = "1000", message = "Count must be less or equal than 1000")
    private long marinesCount;
    @NotNull(message = "Chapter world cannot be null")
    private String world;
}