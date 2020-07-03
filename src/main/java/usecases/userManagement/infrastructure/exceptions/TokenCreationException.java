package usecases.userManagement.infrastructure.exceptions;

public class TokenCreationException extends Exception{

   public TokenCreationException(String message,Throwable throwable){
       super(message,throwable);
   }
}
