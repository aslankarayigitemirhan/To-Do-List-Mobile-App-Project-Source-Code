package com.software.controller;

import com.software.model.Tag;
import com.software.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    // Create Tag
    @PostMapping("/create")
    public ResponseEntity<?> createTag(@RequestParam String tagName, @RequestHeader("Authorization") String token) {
        try {
            Tag createdTag = tagService.createTag(tagName, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred during Tag creation");
        }
    }

    // Delete Tag
    @DeleteMapping("/delete/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable Long tagId, @RequestHeader("Authorization") String token) {
        try {
            String result = tagService.deleteTag(tagId, token);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tag not found or unauthorized");
        }
    }

    // Update Tag Name
    @PutMapping("/update/{tagId}")
    public ResponseEntity<?> updateTagName(@PathVariable Long tagId, @RequestParam String newTagName, @RequestHeader("Authorization") String token) {
        try {
            Tag updatedTag = tagService.updateTagName(tagId, newTagName, token);
            return ResponseEntity.status(HttpStatus.OK).body(updatedTag);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tag not found or unauthorized");
        }
    }
}
