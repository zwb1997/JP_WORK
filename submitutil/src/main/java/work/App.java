package work;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import work.constants.InitViewState;
import work.service.AutoServiceEntry;

@SpringBootApplication
public class App implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    @Autowired
    private AutoServiceEntry serviceEntry;

    public static void main(String[] args) {
        LOG.info("app start");
        new InitViewState();
        SpringApplication application = new SpringApplication(App.class);
        ConfigurableApplicationContext appContext = application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("start some services...");
        List<String> goodIds = new ArrayList<>();
        // Collections.addAll(goodIds, new String[] { "5201230113", "5201230117", "5201230119" });
        Collections.addAll(goodIds, new String[] { "5101030027" });
        boolean serviceFlag = serviceEntry.run(goodIds);

    }
}
