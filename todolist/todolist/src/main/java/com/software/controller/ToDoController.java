package com.software.controller;

import com.software.model.Priority;
import com.software.model.Tag;
import com.software.model.ToDo;
import com.software.service.ToDoService;
import com.software.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/todos")
public class ToDoController {

    private final ToDoService toDoService;
    private final JwtUtil jwtUtil;

    @Autowired
    public ToDoController(ToDoService toDoService, JwtUtil jwtUtil) {
        this.toDoService = toDoService;
        this.jwtUtil = jwtUtil;
    }

    // Create To Do
    @PostMapping("/create")
    public ResponseEntity<?> createToDo(@RequestBody ToDo newToDo, @RequestHeader("Authorization") String token) {
        try {
            ToDo createdToDo = toDoService.createToDo(
                    newToDo.getTodotitle(),
                    newToDo.getTodoDetailedDescription(),
                    newToDo.getStartingDate(),
                    newToDo.getExpectedEndTime(),
                    token
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(createdToDo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred during ToDo creation");
        }
    }

    // Delete To Do
    @DeleteMapping("/delete/{toDoId}")
    public ResponseEntity<?> deleteToDo(@PathVariable Long toDoId, @RequestHeader("Authorization") String token) {
        try {
            String result = toDoService.deleteToDo(toDoId, token);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ToDo not found or unauthorized");
        }
    }

    // Update Priority
    @PutMapping("/update-priority/{toDoId}")
    public ResponseEntity<?> updatePriority(@PathVariable Long toDoId, @RequestParam Priority newPriority, @RequestHeader("Authorization") String token) {
        try {
            ToDo updatedToDo = toDoService.updatePriority(toDoId, newPriority, token);
            return ResponseEntity.status(HttpStatus.OK).body(updatedToDo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ToDo not found or unauthorized");
        }
    }

    // Update Dates (Start Date and Expected End Date)
    @PutMapping("/update-dates/{toDoId}")
    public ResponseEntity<?> updateDates(@PathVariable Long toDoId,
                                         @RequestParam LocalDate newStartDate,
                                         @RequestParam LocalDate newExpectedEndDate,
                                         @RequestHeader("Authorization") String token) {
        try {
            ToDo updatedToDo = toDoService.updateDates(toDoId, newStartDate, newExpectedEndDate, token);
            return ResponseEntity.status(HttpStatus.OK).body(updatedToDo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ToDo not found or unauthorized");
        }
    }

    // Update Title
    @PutMapping("/update-title/{toDoId}")
    public ResponseEntity<?> updateTitle(@PathVariable Long toDoId, @RequestParam String newTitle, @RequestHeader("Authorization") String token) {
        try {
            ToDo updatedToDo = toDoService.updateTitle(toDoId, newTitle, token);
            return ResponseEntity.status(HttpStatus.OK).body(updatedToDo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ToDo not found or unauthorized");
        }
    }

    // Update Description
    @PutMapping("/update-description/{toDoId}")
    public ResponseEntity<?> updateDescription(@PathVariable Long toDoId, @RequestParam String newDescription, @RequestHeader("Authorization") String token) {
        try {
            ToDo updatedToDo = toDoService.updateDescription(toDoId, newDescription, token);
            return ResponseEntity.status(HttpStatus.OK).body(updatedToDo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ToDo not found or unauthorized");
        }
    }

    // Add Tag
    @PutMapping("/add-tag/{toDoId}")
    public ResponseEntity<?> addTag(@PathVariable Long toDoId, @RequestParam Long tagId, @RequestHeader("Authorization") String token) {
        try {
            ToDo updatedToDo = toDoService.addTag(toDoId, tagId, token);
            return ResponseEntity.status(HttpStatus.OK).body(updatedToDo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ToDo or Tag not found, or unauthorized");
        }
    }

    // Delete Tag
    @DeleteMapping("/delete-tag/{toDoId}")
    public ResponseEntity<?> deleteTag(@PathVariable Long toDoId, @RequestParam Long tagId, @RequestHeader("Authorization") String token) {
        try {
            ToDo updatedToDo = toDoService.deleteTag(toDoId, tagId, token);
            return ResponseEntity.status(HttpStatus.OK).body(updatedToDo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ToDo or Tag not found, or unauthorized");
        }
    }
}
