package com.liquido.aqueducts.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck(final HttpServletRequest request) {
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

    @GetMapping("/version")
    public ResponseEntity<String> version() {
        String version = "v2024.01.11.1";
        return new ResponseEntity<>(version, HttpStatus.OK);
    }

}
