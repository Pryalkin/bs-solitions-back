package com.pryalkin.bs_solutions.controller;

import com.pryalkin.bs_solutions.dto.DowntimeDto;
import com.pryalkin.bs_solutions.service.ForkliftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DowntimeController {
    private final ForkliftService service;

    @GetMapping("/api/forklifts/{forkliftId}/downtimes")
    public List<DowntimeDto> list(@PathVariable Long forkliftId) {
        return service.getDowntimesByForklift(forkliftId);
    }

    @PostMapping("/api/forklifts/{forkliftId}/downtimes")
    public ResponseEntity<?> create(@PathVariable Long forkliftId, @RequestBody DowntimeDto dto) {
        try {
            if (dto.getStartDate() == null) {
                dto.setStartDate(java.time.LocalDateTime.now());
            }
            return ResponseEntity.ok(service.createDowntime(forkliftId, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/api/downtimes/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody DowntimeDto dto) {
        try {
            return ResponseEntity.ok(service.updateDowntime(id, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/api/downtimes/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.deleteDowntime(id);
        return ResponseEntity.ok().build();
    }
}

