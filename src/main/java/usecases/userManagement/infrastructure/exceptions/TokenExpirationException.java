package usecases.userManagement.infrastructure.exceptions;

public class TokenExpirationException extends Exception {
    public TokenExpirationException(String message,Throwable throwable){
        super(message,throwable);
    }
}
