package com.btto.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/test")
    public String getStatus() {
        return "It's Alive!!!";
    }
}
