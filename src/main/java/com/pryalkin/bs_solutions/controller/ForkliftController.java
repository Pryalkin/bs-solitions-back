package com.pryalkin.bs_solutions.controller;

import com.pryalkin.bs_solutions.model.Forklift;
import com.pryalkin.bs_solutions.service.ForkliftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forklifts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ForkliftController {
    private final ForkliftService service;

    @GetMapping
    public List<Forklift> search(@RequestParam(required = false) String number) {
        return service.getForklifts(number);
    }

    @PostMapping
    public Forklift create(@RequestBody Forklift forklift) {
        return service.saveForklift(forklift, "Текущий Пользователь");
    }

    @PutMapping("/{id}")
    public Forklift update(@PathVariable Long id, @RequestBody Forklift forklift) {
        forklift.setId(id);
        return service.saveForklift(forklift, "Текущий Пользователь");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.deleteForklift(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
