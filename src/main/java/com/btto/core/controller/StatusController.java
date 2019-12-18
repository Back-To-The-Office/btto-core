package com.btto.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class StatusController {

    @GetMapping("/status")
    public String getStatus() {
        return "It's Alive!!!";
    }

}
