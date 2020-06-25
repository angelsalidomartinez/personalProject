package usecases.userManagement.infrastructure.dto;

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

    public UserBuilder withToken(String token){
        user.setToken(token);
        return this;
    }

    public User build(){
        return user;
    }
}
