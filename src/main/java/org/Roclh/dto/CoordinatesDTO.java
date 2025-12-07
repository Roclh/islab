package org.Roclh.dto;

import jakarta.validation.constraints.DecimalMax;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class CoordinatesDTO {

    @NotNull(message = "X coordinate cannot be null")
    @DecimalMax(value = "535", inclusive = false, message = "X must be less than 535")
    private Integer x;

    private long y;
}
