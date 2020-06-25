package usecases.userManagement.infrastructure.healthcheck;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "healthcheck")
public class HealthCheck {

    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }

}
