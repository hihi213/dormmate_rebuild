package com.dormmate.api.debug;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug/validate")
public class DebugValidationController {

    @PostMapping
    public ResponseEntity<Void> validate(@Valid @RequestBody DebugValidationRequest request) {
        return ResponseEntity.noContent().build();
    }
}
