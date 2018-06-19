package com.sohu.mp.sharingplan.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @RequestMapping({"", "/health"})
    public String health() {
        return "health";
    }

}
