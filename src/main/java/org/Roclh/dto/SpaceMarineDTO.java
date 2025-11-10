package org.Roclh.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import org.Roclh.model.MeleeWeapon;

@Data
public class SpaceMarineDTO {
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Coordinates cannot be null")
    private CoordinatesDTO coordinates;

    @NotNull(message = "Chapter cannot be null")
    private Long chapterId;

    @NotNull(message = "Health cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Health must be greater than 0")
    private Float health;

    @Min(value = 1, message = "Heart count must be at least 1")
    @Max(value = 3, message = "Heart count cannot exceed 3")
    private int heartCount;

    private Long height;

    @NotNull(message = "Melee weapon cannot be null")
    private MeleeWeapon meleeWeapon;
}