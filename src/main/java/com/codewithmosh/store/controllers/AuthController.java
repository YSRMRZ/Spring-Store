package com.codewithmosh.store.controllers;

import com.codewithmosh.store.DTO.JwtResponse;
import com.codewithmosh.store.DTO.LoginRequest;
import com.codewithmosh.store.DTO.UserDto;
import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.services.AuthService;
import com.codewithmosh.store.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
      @Valid @RequestBody LoginRequest request,
      HttpServletResponse response
    ){
       authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                       request.getEmail(),
                       request.getPassword()
               )
       );

       var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

       var accessToken = jwtService.generateAccessToken(user);
       var refreshToken = jwtService.generateRefreshToken(user);

       var cookie = new Cookie("refreshToken", refreshToken);
       cookie.setHttpOnly(true);
       cookie.setPath("/auth/refresh");
       cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration()); //7 days
       cookie.setSecure(false);// IMPORTANT for HTTP localhost set true on production
       response.addCookie(cookie);
       return ResponseEntity.ok(new JwtResponse(accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @CookieValue(value = "refreshToken") String refreshToken
    ){
        if(!jwtService.validateToken(refreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var userId =jwtService.getUserIdFromToken(refreshToken);
        var user = userRepository.findById(userId).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(new JwtResponse(accessToken));
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestHeader("Authorization") String authHeader) {
        var token = authHeader.replace("Bearer ", "");
        return jwtService.validateToken(token);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(){
        var user = authService.getCurrentUser();
        if(user == null){
            return ResponseEntity.notFound().build();
        }

        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
