package usecases.userManagement.infrastructure.controllers;

import org.springframework.web.bind.annotation.*;
import usecases.userManagement.application.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import usecases.userManagement.infrastructure.dto.User;

import javax.validation.Valid;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/authenticate")
    public boolean authenticate(@RequestParam(value = "email") String email, @RequestParam(value ="password") String password){
        return userService.authenticate(email, password);
    }

    @PutMapping("/registerUser")
    public User registerUser(@Valid @RequestBody User user){
        return userService.registerUser(user);
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
