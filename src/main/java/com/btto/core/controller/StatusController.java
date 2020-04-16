package com.btto.core.controller;

import com.btto.core.domain.User;
import com.btto.core.spring.CurrentUser;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log
public class StatusController extends ApiV1AbstractController {

    @GetMapping("/status")
    public String getStatus() {
        return "It's Alive!!!";
    }

    @GetMapping("/protected")
    public String getProtected(@CurrentUser User currentUser) {
        log.info("User email " + currentUser.getEmail() + " id " + currentUser.getId());
        return "It's Alive!!!";
    }

}
