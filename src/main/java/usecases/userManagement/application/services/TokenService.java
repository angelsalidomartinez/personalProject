package usecases.userManagement.application.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.codehaus.groovy.syntax.TokenException;
import org.springframework.stereotype.Service;
import usecases.userManagement.infrastructure.exceptions.TokenCreationException;
import usecases.userManagement.infrastructure.exceptions.TokenExpirationException;

import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class TokenService {

    Algorithm algorithmToSignToken = Algorithm.HMAC512("secret");

    public String create() throws TokenCreationException {
        String token = "";
        try{
            LocalDate expirationTime = LocalDateTime.now().plusDays(10).toLocalDate();
            Date expirationDate = Date.from(expirationTime.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            token = JWT.create().withIssuer("auth0").withExpiresAt(expirationDate).sign(algorithmToSignToken);
        }catch(JWTCreationException jwtCreationException){
            throw new TokenCreationException(jwtCreationException.getMessage(),jwtCreationException);
        }
        return token;
    }

    public Boolean validate (String token){
        JWTVerifier verifier = JWT.require(algorithmToSignToken).withIssuer("auth0").build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT != null;
    }

    public String expire(String token) throws TokenExpirationException {
        String expiredToken ="";
        try{
            LocalDate expiredDate = LocalDateTime.now().minusDays(10).toLocalDate();
            Date expirationDate = Date.from(expiredDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            expiredToken = JWT.create().withIssuer("auth0").withExpiresAt(expirationDate).sign(algorithmToSignToken);
        }catch(JWTCreationException jwtCreationException){
            throw new TokenExpirationException(jwtCreationException.getMessage(),jwtCreationException);
        }
        return expiredToken;
    }
}
