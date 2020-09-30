package usecases.userManagement.domain.entities.builders;

import usecases.userManagement.domain.entities.User;

public class UserBuilder {

    private User user;

    public UserBuilder(){
        user = new User();
    }

    public UserBuilder withEmail(String emailParam){
        user.setEmail(emailParam);
        return this;
    }

    public UserBuilder withPassword(String passwordParam){
        user.setPassword(passwordParam);
        return this;
    }

    public UserBuilder withToken(String tokenParam){
        user.setToken(tokenParam);
        return this;
    }

    public UserBuilder withStatus(String status){
        user.setStatus(status);
        return this;
    }

    public User build(){
        return user;
    }
}
