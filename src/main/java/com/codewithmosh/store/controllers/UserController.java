package com.codewithmosh.store.controllers;

import com.codewithmosh.store.DTO.ChangePasswordRequest;
import com.codewithmosh.store.DTO.RegisterUserRequest;
import com.codewithmosh.store.DTO.UpdateUserRequest;
import com.codewithmosh.store.DTO.UserDto;
import com.codewithmosh.store.entities.Role;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/users")

public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<UserDto> getAllUsers(
     @RequestHeader(name = "x-auth-token") String authToken,
     @RequestParam(required = false,defaultValue = " ", name = "sortBy") String sortBy
    ){
        System.out.println(authToken);
        if(!Set.of("name", "email").contains(sortBy))
            sortBy = "name";
       return userRepository.findAll(Sort.by(sortBy))
               .stream()
               .map(userMapper::toDto)
               .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id){
        var user =userRepository.findById(id).orElse(null);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping("/create")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder){
       if(userRepository.existsByEmail(request.getEmail())){
           return ResponseEntity.status(HttpStatus.CONFLICT).body(
                   Map.of("email","Email is already registered.")
           );
       }

        var user =userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        var userDto = userMapper.toDto(user);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable(name = "id") Long id,
            @RequestBody UpdateUserRequest request){
        var user =userRepository.findById(id).orElse(null);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        userMapper.update(request,user);
        userRepository.save(user);
        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") Long id){
        var user =userRepository.findById(id).orElse(null);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(user);
        return ResponseEntity.noContent().build();

    }

    @PostMapping("{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable(name = "id") Long id,
            @RequestBody ChangePasswordRequest request){
        var user =userRepository.findById(id).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }
        if (!user.getPassword().equals(request.getOldPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

}
