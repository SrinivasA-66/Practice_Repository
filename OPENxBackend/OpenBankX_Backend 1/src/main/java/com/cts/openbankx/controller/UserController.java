package com.cts.openbankx.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cts.openbankx.model.User;
import com.cts.openbankx.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return service.findById(id);
    }


    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(user));
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {

        User existingUser = service.findById(id);

        if (user.getName() != null)   existingUser.setName(user.getName());
        if (user.getEmail() != null)  existingUser.setEmail(user.getEmail());
        if (user.getPhone() != null)  existingUser.setPhone(user.getPhone());
        if (user.getRole() != null)   existingUser.setRole(user.getRole());
        if (user.getStatus() != null) existingUser.setStatus(user.getStatus());

        return service.save(existingUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
