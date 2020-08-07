package usecases.userManagement.infrastructure.controllers;

import org.springframework.web.bind.annotation.*;
import usecases.userManagement.application.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import usecases.userManagement.infrastructure.dto.User;
import usecases.userManagement.infrastructure.exceptions.UserAutenticationException;
import usecases.userManagement.infrastructure.exceptions.UserCreationException;
import usecases.userManagement.infrastructure.exceptions.UserExpirationException;

import javax.validation.Valid;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/authenticate")
    public boolean authenticate(@RequestParam(value = "email") String email, @RequestParam(value ="password") String password) throws UserAutenticationException {
        return userService.authenticate(email, password);
    }

    @PutMapping("/registerUser")
    public User registerUser(@Valid @RequestBody User user) throws UserCreationException {
        return userService.registerUser(user);
    }

    public boolean logout(@RequestParam(value = "email") String email) throws UserExpirationException {
        return userService.logout(email);
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
