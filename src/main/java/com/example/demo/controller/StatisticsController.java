package com.example.demo.controller;

import com.example.demo.dto.CatchRequest;
import com.example.demo.dto.CatchResponse;
import com.example.demo.enums.FishType;
import com.example.demo.service.CatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final CatchService catchService;

    @GetMapping("/addFish")
    public String addFishPage(Model model) {
        model.addAttribute("catchRequest", new CatchRequest());
        model.addAttribute("fishTypes", FishType.values());
        return "addFish";
    }

    @PostMapping("/addFish")
    public String addNewFish(@ModelAttribute CatchRequest catchRequest,
                             @RequestParam(name = "picture", required = false) MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            catchService.createCatch(catchRequest, file, username);
            redirectAttributes.addFlashAttribute("success", "Catch added successfully!");
            return "redirect:/statistics/myFish";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/statistics/addFish";
        }
    }

    @GetMapping("/myFish")
    public String myFish(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CatchResponse> catches = catchService.getAllUserCatches(username);
        model.addAttribute("allFish", catches);
        return "allFish";
    }

    @GetMapping("/myFish/view/{id}")
    public String viewFish(@PathVariable Long id, Model model) {
        CatchResponse catchDetails = catchService.getCatchDetails(id);
        model.addAttribute("fish", catchDetails);
        return "viewFish";
    }

    @PostMapping("/myFish/delete/{id}")
    public String deleteFish(@PathVariable Long id) {
        catchService.deleteCatch(id);
        return "redirect:/statistics/myFish";
    }
}