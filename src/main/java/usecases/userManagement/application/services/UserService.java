package usecases.userManagement.application.services;

import usecases.userManagement.domain.entities.User;
import usecases.userManagement.infrastructure.dto.UserBuilder;
import usecases.userManagement.infrastructure.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    TokenService tokenService;

    public boolean authenticate(String email, String password){
        Optional<User> safeUserByMail = Optional.ofNullable(usersRepository.findByEmail(email));
        return safeUserByMail.isPresent() && validateCredentials(safeUserByMail.get(),email,password);
    }

    private boolean validateCredentials(User user,String email, String password){
        return email.equalsIgnoreCase(user.getEmail()) && password.equalsIgnoreCase(user.getPassword())
                && tokenService.validate(user.getToken());
    }

    public usecases.userManagement.infrastructure.dto.User registerUser(usecases.userManagement.infrastructure.dto.User user) {
        User domainUser = new usecases.userManagement.domain.entities.builders.UserBuilder()
                .withEmail(user.getEmail())
                .withPassword(user.getPassword())
                .withToken(tokenService.create())
                .build();

        User storedDomainUser = usersRepository.save(domainUser);

        return new UserBuilder()
                .withEmail(storedDomainUser.getEmail())
                .withPassword(storedDomainUser.getPassword())
                .withToken(storedDomainUser.getToken())
                .build();
    }
}
