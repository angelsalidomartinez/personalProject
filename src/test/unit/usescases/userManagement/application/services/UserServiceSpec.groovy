package usescases.userManagement.application.services

import spock.lang.Specification
import spock.lang.Unroll
import usecases.userManagement.application.services.TokenService
import usecases.userManagement.application.services.UserService
import usecases.userManagement.domain.entities.builders.UserBuilder
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

}
