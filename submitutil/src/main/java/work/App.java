package work;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * need three account go fetch "5201230113","5201230117","5201230119" fetch web
 * page find the good counld buy and take order
 * 
 * ATTENTION!!! change department time when local test!
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOG.info("app start");
        SpringApplication application = new SpringApplication(App.class);
        ConfigurableApplicationContext appContext = application.run(args);
    }
}
