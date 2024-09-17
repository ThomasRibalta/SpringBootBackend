package dev.thomasrba.ulib.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/library")
public class LibraryController {

    @PostMapping
    public String createLibrary() {
        return "Library created";
    }
}
