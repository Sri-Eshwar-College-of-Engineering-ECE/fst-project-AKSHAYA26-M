package com.symptomchecker.controller;

import com.symptomchecker.model.SymptomCheck;
import com.symptomchecker.model.SymptomRequest;
import com.symptomchecker.service.SymptomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SymptomController {

    @Autowired
    private SymptomService symptomService;

    @PostMapping("/check")
    public ResponseEntity<SymptomCheck> checkSymptoms(@RequestBody SymptomRequest request) {
        SymptomCheck result = symptomService.analyzeSymptoms(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<List<SymptomCheck>> getHistory() {
        return ResponseEntity.ok(symptomService.getRecentChecks());
    }

    @GetMapping("/symptoms")
    public ResponseEntity<List<String>> getSymptomsList() {
        return ResponseEntity.ok(symptomService.getAllSymptoms());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(symptomService.getStats());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Smart Symptom Checker is running!");
    }
}
