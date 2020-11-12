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

import work.constants.BaseParameters;
import work.model.GoodModel;
import work.service.orderservice.AutoServiceEntry;

/**
 * need three account go fetch "5201230113","5201230117","5201230119" fetch web
 * page find the good counld buy and take order
 */
@SpringBootApplication
public class App implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    @Autowired
    private AutoServiceEntry serviceEntry;

    public static void main(String[] args) {
        LOG.info("app start");
        SpringApplication application = new SpringApplication(App.class);
        ConfigurableApplicationContext appContext = application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("start some services...");
        // Collections.addAll(goodIds, new String[] { "5201230113",
        // "5201230117","5201230119" });
        GoodModel gm1 = new GoodModel("5101030027", 1);
        Collections.addAll(BaseParameters.GOOD_IDS, gm1);
        boolean serviceFlag = serviceEntry.run();

    }
}
