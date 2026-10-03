package org.example.userservice.controllers;

import org.example.userservice.dtos.LoginRequestDto;
import org.example.userservice.dtos.SignupRequestDto;
import org.example.userservice.dtos.TokenDto;
import org.example.userservice.dtos.UserDto;
import org.example.userservice.exceptions.EmailTakenException;
import org.example.userservice.exceptions.InvalidPasswordException;
import org.example.userservice.exceptions.InvalidTokenException;
import org.example.userservice.exceptions.NotFoundException;
import org.example.userservice.models.Token;
import org.example.userservice.models.User;
import org.example.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "User Service is running!";
    }

    @PostMapping("/signup")//create user object
    public UserDto signup(@RequestBody SignupRequestDto signupRequestDto) throws EmailTakenException {
        User user = userService.signup(
                signupRequestDto.getName(),
                signupRequestDto.getEmail(),
                signupRequestDto.getPassword()
        );

        return UserDto.from(user);
    }

    @PostMapping("/login")//create token object
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequestDto loginRequestDto) throws NotFoundException, InvalidPasswordException {
        Token token = userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        if(token == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return  new ResponseEntity<>(TokenDto.from(token), HttpStatus.OK);
    }

    @GetMapping("/validate/{tokenValue}")
    public ResponseEntity<UserDto> validateToken(@PathVariable String tokenValue) throws InvalidTokenException {
        User user = userService.validateToken(tokenValue);

        //
        if(user == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //Token is valid
        return new ResponseEntity<>(UserDto.from(user), HttpStatus.OK);
    }

    //TODO-logout, forgotPassword
}