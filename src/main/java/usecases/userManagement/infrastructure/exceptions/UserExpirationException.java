package usecases.userManagement.infrastructure.exceptions;

public class UserExpirationException extends Exception{

    public UserExpirationException(String message, Throwable throwable){
        super(message,throwable);
    }

}
