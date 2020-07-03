package usecases.userManagement.application.services;

import org.apache.commons.lang3.StringUtils;
import usecases.userManagement.domain.entities.User;
import usecases.userManagement.infrastructure.dto.UserBuilder;
import usecases.userManagement.infrastructure.exceptions.TokenCreationException;
import usecases.userManagement.infrastructure.exceptions.UserAutenticationException;
import usecases.userManagement.infrastructure.exceptions.UserCreationException;
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

    public boolean authenticate(String email, String password) throws UserAutenticationException {

        try{
            if(!credentialsAreFulFilled(email,password)){
                return false;
            }

            Optional<User> safeUserByMail = Optional.ofNullable(usersRepository.findByEmail(email));
            if(!safeUserByMail.isPresent()){
                return false;
            }
            if(!validateCredentials(safeUserByMail.get(),email,password)){
                return false;
            }

            User userInDataBase = safeUserByMail.get();

            if(!tokenService.validate(userInDataBase.getToken())){
                userInDataBase.setToken(tokenService.create());
                usersRepository.save(userInDataBase);
            }
        }catch(TokenCreationException tokenCreationException){
            throw new UserAutenticationException("Cannot create the token",tokenCreationException);
        }catch (Exception exception){
            throw new UserAutenticationException("GeneralException: "+ exception.getMessage(),exception);
        }

        return true;
    }

    private boolean credentialsAreFulFilled(String email, String password) {
        return StringUtils.isNotBlank(email) && StringUtils.isNotBlank(password)
                && StringUtils.isNotEmpty(email) && StringUtils.isNotEmpty(password);
    }

    private boolean validateCredentials(User user,String email, String password){
        return email.equalsIgnoreCase(user.getEmail()) && password.equalsIgnoreCase(user.getPassword());
    }

    public usecases.userManagement.infrastructure.dto.User registerUser(usecases.userManagement.infrastructure.dto.User user) throws UserCreationException {
        User domainUser = null;
        User storedDomainUser = null;
        try {
            domainUser = new usecases.userManagement.domain.entities.builders.UserBuilder()
                    .withEmail(user.getEmail())
                    .withPassword(user.getPassword())
                    .withToken(tokenService.create())
                    .build();

        storedDomainUser = usersRepository.save(domainUser);

        } catch (TokenCreationException e) {
            throw new UserCreationException("User cannot be registered", e);
        } catch (Exception e){
            throw new UserCreationException("User cannot be registered: " + e.getMessage(), e);
        }
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
