package usecases.userManagement.infrastructure.exceptions;

public class UserCreationException extends Exception{
    public UserCreationException(String message, Throwable throwable){
        super(message,throwable);
    }
}
