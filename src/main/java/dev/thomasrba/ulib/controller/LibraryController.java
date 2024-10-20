package dev.thomasrba.ulib.controller;

import org.apache.tomcat.jni.Library;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "api/v1/library")
public class LibraryController {

    @PostMapping
    public String createLibrary() {
        return "Library created";
    }

    @PutMapping("/{id}")
    public String updateLibrary(@PathVariable Long id) { return "Library updated"; }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE) // Tmp Map replace with lib object
    public Map<String, String> getLibrary(@PathVariable Long id) {
        Map<String, String> library = new HashMap<>();
        library.put("id", String.valueOf(id));
        return library;
    }

    @DeleteMapping("{id}")
    public String deleteLibrary(@PathVariable Long id) { return "Library deleted"; }
}
