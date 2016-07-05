package wz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import wz.parser.Parser;
import wz.store.FlightStore;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Created by florinbotis on 05/07/2016.
 */
@SpringBootApplication
public class MainParser {
    private static final Logger log = LoggerFactory.getLogger(MainParser.class);

    public static void main(String[] args) {
        SpringApplication.run(MainParser.class, args);
    }

    @Bean
    public CommandLineRunner start(FlightStore repository) {
        return (args) -> {
            while (true) {
                log.info("------->BEFORE:" + repository.count());
                Parser parser = new Parser(Paths.get(args[0]), repository);
                parser.parse();
                log.info("------->AFTER:" + repository.count());

                log.info("Sleeping 1 hour");
                TimeUnit.HOURS.sleep(1);
            }
        };

    }
}
