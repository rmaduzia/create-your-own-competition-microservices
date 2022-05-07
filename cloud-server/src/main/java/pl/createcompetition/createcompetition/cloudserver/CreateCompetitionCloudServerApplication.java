package pl.createcompetition.createcompetition.cloudserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class CreateCompetitionCloudServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreateCompetitionCloudServerApplication.class, args);
    }

}
