package usecases.userManagement.application.services;

import org.apache.commons.lang3.StringUtils;
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
        boolean isAuthenticated = false;
        if(credentialsAreFulFilled(email,password)){
            Optional<User> safeUserByMail = Optional.ofNullable(usersRepository.findByEmail(email));
            if(!safeUserByMail.isPresent()){
                return isAuthenticated;
            }
            if(!validateCredentials(safeUserByMail.get(),email,password)){
                return isAuthenticated;
            }

            User userInDataBase = safeUserByMail.get();

            if(!tokenService.validate(userInDataBase.getToken())){
                userInDataBase.setToken(tokenService.create());
                usersRepository.save(userInDataBase);
            }
            isAuthenticated = true;
        }
        return isAuthenticated;
    }

    private boolean credentialsAreFulFilled(String email, String password) {
        return StringUtils.isNotBlank(email) && StringUtils.isNotBlank(password)
                && StringUtils.isNotEmpty(email) && StringUtils.isNotEmpty(password);
    }

    private boolean validateCredentials(User user,String email, String password){
        return email.equalsIgnoreCase(user.getEmail()) && password.equalsIgnoreCase(user.getPassword());
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

    public void setUsersRepository(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }
}
