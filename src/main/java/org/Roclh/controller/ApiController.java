package org.Roclh.controller;

import lombok.RequiredArgsConstructor;
import org.Roclh.model.SpaceMarine;
import org.Roclh.service.SpaceMarineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/spacemarines")
@RequiredArgsConstructor
public class ApiController {

    private final SpaceMarineService spaceMarineService;

    @GetMapping("/{id}")
    public ResponseEntity<SpaceMarine> getById(@PathVariable Long id) {
        Optional<SpaceMarine> spaceMarine = spaceMarineService.findById(id);
        return spaceMarine.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
