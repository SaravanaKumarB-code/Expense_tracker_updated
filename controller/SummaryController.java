package com.expenseTracker.controller;

import com.expenseTracker.model.User;
import com.expenseTracker.service.AuthService;
import com.expenseTracker.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/summary")
@CrossOrigin(origins = "*")
public class SummaryController {

    @Autowired
    private SummaryService summaryService;
    
    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<?> getSummary(@RequestParam Long userId) {
        Optional<User> userOptional = authService.getUserById(userId);
        
        if (!userOptional.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        
        return ResponseEntity.ok(summaryService.getSummaryForUser(userOptional.get()));
    }
}