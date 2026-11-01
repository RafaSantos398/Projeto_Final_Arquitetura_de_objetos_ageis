package com.berxy.projeto.user;

import com.berxy.projeto.user.dto.ChangePasswordRequest;
import com.berxy.projeto.user.dto.CreateUserRequest;
import com.berxy.projeto.user.dto.UpdateUserRequest;
import com.berxy.projeto.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid CreateUserRequest request) {
        User user = userService.create(request.nome(), request.email(), request.password());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();

        return ResponseEntity.created(location).body(UserResponse.from(user));
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return UserResponse.from(userService.findById(id));
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest request) {
        User user = userService.update(id, request.nome(), request.email());
        return UserResponse.from(user);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,
                                               @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(id, request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        userService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}