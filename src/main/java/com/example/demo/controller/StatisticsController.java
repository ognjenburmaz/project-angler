package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/statistics")
public class StatisticsController {

    @GetMapping(value = "/addFish")
    public String addFish() {
        return "addFish";
    }
}
