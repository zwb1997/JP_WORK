package work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import work.constants.InitViewState;
import work.service.AutoLoginService;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    @Autowired
    private AutoLoginService loginService;

    public static void main(String[] args) {
        LOG.info("app start");
        new InitViewState();
        SpringApplication application = new SpringApplication(App.class);
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("start some services...");
        loginService.login();
    }
}
