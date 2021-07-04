package ru.mashka.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import ru.mashka.services.PageService;

@Controller
public class FindPageController {
    private String successMessage = "HTML page of web site is successfully downloaded! " +
            "You can see count of words in console";
    @Autowired
    PageService pageService;
    @GetMapping("/find")
    public String FindPage(@RequestParam String url, Model model) throws IOException {
        String message;
        model.addAttribute("success", "");
        model.addAttribute("error", "");
        pageService.setStringURL(url);
        if (!pageService.isValidURL()) {
            model.addAttribute("error", "Invalid URL");
            return "start_page";
        }
        message = pageService.createFile();
        if (!message.equals("OK")) {
            model.addAttribute("error", message);
            return "start_page";
        }
        message = pageService.printTextInfo();
        if (!message.equals("OK")) {
            model.addAttribute("error", message);
            return "start_page";
        }
        model.addAttribute("success", successMessage);
        return "start_page";
    }
    @GetMapping()
    public String start(Model model) {
        model.addAttribute("success", "");
        model.addAttribute("error", "");
        return "start_page";
    }
}
