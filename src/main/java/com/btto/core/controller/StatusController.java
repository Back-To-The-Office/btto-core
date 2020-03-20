package com.btto.core.controller;

import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Log
public class StatusController extends ApiV1AbstractController {

    @GetMapping("/status")
    public String getStatus() {
        return "It's Alive!!!";
    }

    @GetMapping("/protected")
    public String getProtected(final Principal principal) {
        log.info("User " + principal.getName());
        System.out.println(principal.getName());
        return "It's Alive!!!";
    }

}
