package org.Roclh.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Embeddable
public class Coordinates {

    @NotNull(message = "X coordinate cannot be null")
    @DecimalMax(value = "535", inclusive = false, message = "X must be less than 535")
    private Integer x;

    private long y;
}
