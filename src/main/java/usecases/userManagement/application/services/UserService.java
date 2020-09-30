package usecases.userManagement.application.services;

import org.apache.commons.lang3.StringUtils;
import usecases.userManagement.domain.entities.User;
import usecases.userManagement.infrastructure.dto.UserBuilder;
import usecases.userManagement.infrastructure.exceptions.*;
import usecases.userManagement.infrastructure.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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

            if(!validateAccountActivated(safeUserByMail.get())){
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

    private boolean validateAccountActivated(User user) {
        return "ACTIVATED".equalsIgnoreCase(user.getStatus());
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
                    .withStatus("PENDING_TO_ACTIVATE")
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

    public boolean logout (String email) throws UserExpirationException {
        Optional<User> safeUserByMail = Optional.ofNullable(usersRepository.findByEmail(email));
        if(safeUserByMail.isPresent()){
            User userInDataBase = safeUserByMail.get();
            if (tokenService.validate(userInDataBase.getToken())){
                try {
                    userInDataBase.setToken(tokenService.expire(userInDataBase.getToken()));
                } catch (TokenExpirationException e) {
                    throw new UserExpirationException("User cannot log out: "+ e.getMessage(),e);
                }
            }
        }
        return true;
    }

    public usecases.userManagement.infrastructure.dto.User activate(String emailEncrypted) {

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
            String passPhrase = "enabler";
            SecretKeySpec key = new SecretKeySpec(passPhrase.getBytes(), "AES");
            //cipher.init(Cipher.DECRYPT_MODE, key);
            //byte[] plainText = new byte[];
            //int ptLength = cipher.update(emailEncrypted, 0, ctLength, plainText, 0);
            //ptLength += cipher.doFinal(plainText, ptLength);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }/* catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (ShortBufferException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    public void setUsersRepository(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }
}
