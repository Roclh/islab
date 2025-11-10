package org.Roclh.dto;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Data
public class CoordinatesDTO {

    @NotNull(message = "X coordinate cannot be null")
    @DecimalMin(value = "-674", inclusive = false, message = "X must be greater than -675")
    private Double x;

    private float y;
}
