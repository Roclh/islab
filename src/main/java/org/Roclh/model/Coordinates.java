package org.Roclh.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Embeddable
public class Coordinates {

    @NotNull(message = "X coordinate cannot be null")
    @DecimalMin(value = "-674", inclusive = false, message = "X must be greater than -675")
    private Double x;

    private float y;
}
