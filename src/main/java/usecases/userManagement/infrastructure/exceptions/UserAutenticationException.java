package usecases.userManagement.infrastructure.exceptions;

public class UserAutenticationException extends Exception{
    public UserAutenticationException(String message, Throwable throwable){
        super(message,throwable);
    }
}
