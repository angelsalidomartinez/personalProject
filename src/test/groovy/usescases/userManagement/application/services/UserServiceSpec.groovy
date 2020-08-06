package usescases.userManagement.application.services

import com.auth0.jwt.exceptions.JWTCreationException
import spock.lang.Specification
import spock.lang.Unroll
import usecases.userManagement.application.services.TokenService
import usecases.userManagement.application.services.UserService
import usecases.userManagement.domain.entities.builders.UserBuilder
import usecases.userManagement.infrastructure.dto.User
import usecases.userManagement.infrastructure.exceptions.TokenCreationException
import usecases.userManagement.infrastructure.exceptions.UserAutenticationException
import usecases.userManagement.infrastructure.exceptions.UserCreationException
import usecases.userManagement.infrastructure.repositories.UsersRepository

class UserServiceSpec extends Specification{

  @Unroll
  void "return #outcomeExpected if the #email and #password"(){
      given:"an userService class"
      UserService userService = new UserService()
      when:
      boolean isAutenticated = userService.authenticate(email,password)
      then:
      outcomeExpected == isAutenticated
      where:
      email | password || outcomeExpected
      null  | null     || false
      ""    | null     || false
      null  | ""       || false
      ""    | ""       || false
  }

  @Unroll
  void "return false if the email is fakeEmail and password is fakePassword and token is tokenTest" (){
      given:"an userService class"
      UserService userService = new UserService()
      and:"mock the findUser behavior"
      UsersRepository usersRepository = Mock(UsersRepository)
      usersRepository.findByEmail(email)>> userInDb
      userService.usersRepository = usersRepository
      when:
      boolean isAutenticated = userService.authenticate(email,password)
      then:
      isAutenticated == expectedResult
      where:
      email           | password        | token        | userInDb || expectedResult
      "fakeEmail"     | "fakePassword"  | "tokenTest"  | null     || false

  }

    @Unroll
    void "return #expectedResult if the email is #email and password is #password and token is #token" (){
        given:"an userService class"
        UserService userService = new UserService()
        and:"mock the findUser behavior"
        UsersRepository usersRepository = Mock(UsersRepository)
        usersRepository.findByEmail(email)>> userInDb
        userService.usersRepository = usersRepository
        and:"mock the tokenService behavior"
        TokenService tokenService = Mock(TokenService)
        tokenService.validate("tokenTest")>> isValidToken
        userService.tokenService = tokenService
        when:
        boolean isAutenticated = userService.authenticate(email,password)
        then:
        isAutenticated == expectedResult
        where:
        email           | password        | isValidToken | userInDb                                                                                             || expectedResult
        "test@test.com" | "password"      | true         | new UserBuilder().withEmail("test@test.com").withPassword("password").withToken("tokenTest").build() || true
        "test@test.com" | "password"      | false        | new UserBuilder().withEmail("test@test.com").withPassword("password").withToken("tokenTest").build() || true
    }

    void "throw a TokenCreationException if there is any problem when the token is going to be created"(){
        given:"an userService class"
        UserService userService = new UserService()
        and:"mock the findUser behavior"
        UsersRepository usersRepository = Mock(UsersRepository)
        usersRepository.findByEmail(email)>> userInDb
        userService.usersRepository = usersRepository
        and:"mock the tokenService behavior"
        TokenService tokenService = Mock(TokenService)
        tokenService.validate("tokenTest")>> isValidToken
        and:"throws que creation exception"
        tokenService.create() >> {throw new TokenCreationException("error", new JWTCreationException("error",new Exception()))}
        userService.tokenService = tokenService

        when:
        userService.authenticate(email,password)
        then:
        thrown(UserAutenticationException)
        where:
        email           | password        | isValidToken | userInDb
        "test@test.com" | "password"      | false        | new UserBuilder().withEmail("test@test.com").withPassword("password").withToken("tokenTest").build()

    }

    void "throws a UserAutenticationException if there is any problem when the token is going to be created"(){
        given:"an userService class"
        UserService userService = new UserService()
        and:"mock the findUser behavior"
        UsersRepository usersRepository = Mock(UsersRepository)
        usersRepository.findByEmail(email)>> userInDb
        usersRepository.save(userInDb)>> {throw new Exception("Error saving")}
        userService.usersRepository = usersRepository
        and:"mock the tokenService behavior"
        TokenService tokenService = Mock(TokenService)
        tokenService.validate("tokenTest")>> isValidToken
        and:"throws que creation exception"
        tokenService.create() >> {throw new TokenCreationException("error", new JWTCreationException("error",new Exception()))}
        userService.tokenService = tokenService
        when:
        userService.authenticate(email,password)
        then:
        thrown(UserAutenticationException)
        where:
        email           | password        | isValidToken | userInDb
        "test@test.com" | "password"      | false        | new UserBuilder().withEmail("test@test.com").withPassword("password").withToken("tokenTest").build()
    }

    void "throws an UserCreationException if there is any proble when the token is created"(){
        given:"an userService class"
        UserService userService = new UserService()
        and:"mock the findUser behavior"
        UsersRepository usersRepository = Mock(UsersRepository)
        usersRepository.save(userInDb)>> userInDb
        userService.usersRepository = usersRepository
        and:"mock the tokenService behavior"
        TokenService tokenService = Mock(TokenService)
        tokenService.validate("tokenTest")>> isValidToken
        and:"throws que creation exception"
        tokenService.create() >> {throw new TokenCreationException("error", new JWTCreationException("error",new Exception()))}
        userService.tokenService = tokenService
        and:"User to be inserted"
        usecases.userManagement.infrastructure.dto.User userToBeInserted = new usecases.userManagement.infrastructure.dto.UserBuilder().withEmail("test@test.com").withPassword("password").withToken("tokenTest").build()
        when:
        userService.registerUser(userToBeInserted)
        then:
        thrown(UserCreationException)
        where:
        email           | password        | isValidToken | userInDb
        "test@test.com" | "password"      | false        | new UserBuilder().withEmail("test@test.com").withPassword("password").withToken("tokenTest").build()
    }

    void "throws an UserCreationException if there is any problem when the user is stored"(){
        given:"an userService class"
        UserService userService = new UserService()
        and:"mock the findUser behavior"
        UsersRepository usersRepository = Mock(UsersRepository)
        usersRepository.save(_)>> { throw new Exception("Error") }
        userService.usersRepository = usersRepository
        and:"mock the tokenService behavior"
        TokenService tokenService = Mock(TokenService)
        tokenService.validate("tokenTest")>> isValidToken
        and:"throws que creation exception"
        tokenService.create() >> "tokenTest"
        userService.tokenService = tokenService
        and:"User to be inserted"
        usecases.userManagement.infrastructure.dto.User userToBeInserted = new usecases.userManagement.infrastructure.dto.UserBuilder().withEmail("test@test.com").withPassword("password").withToken("tokenTest").build()
        when:
        userService.registerUser(userToBeInserted)
        then:
        thrown(UserCreationException)
        where:
        email           | password        | isValidToken | userInDb
        "test@test.com" | "password"      | false        | new UserBuilder().withEmail("test@test.com").withPassword("password").withToken("tokenTest").build()
    }

    void "throws the user registered if there is any problem when the user is stored"(){
        given:"an userService class"
        UserService userService = new UserService()
        and:"mock the findUser behavior"
        UsersRepository usersRepository = Mock(UsersRepository)
        usersRepository.save(_)>> { userInDb }
        userService.usersRepository = usersRepository
        and:"mock the tokenService behavior"
        TokenService tokenService = Mock(TokenService)
        tokenService.validate("tokenTest")>> isValidToken
        and:"throws que creation exception"
        tokenService.create() >> "tokenTest"
        userService.tokenService = tokenService
        and:"User to be inserted"
        usecases.userManagement.infrastructure.dto.User userToBeInserted = new usecases.userManagement.infrastructure.dto.UserBuilder().withEmail("test@test.com").withPassword("password").withToken("tokenTest").build()
        when:
        usecases.userManagement.infrastructure.dto.User userRegsitered = userService.registerUser(userToBeInserted)
        then:
        userRegsitered.email == userInDb.email
        userRegsitered.password == userInDb.password
        userRegsitered.token == userInDb.token
        where:
        email           | password        | isValidToken | userInDb
        "test@test.com" | "password"      | false        | new UserBuilder().withEmail("test@test.com").withPassword("password").withToken("tokenTest").build()
    }

}
