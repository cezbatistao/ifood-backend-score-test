package ifood.score.infrastructure.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan(basePackages = "ifood.score")
@EnableJms
@EnableReactiveMongoRepositories
@AutoConfigureAfter(EmbeddedMongoAutoConfiguration.class)
@RequiredArgsConstructor
public class Application /*extends AbstractReactiveMongoConfiguration*/ {

    private final Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

//    @Override
//    @Bean
//    @DependsOn("embeddedMongoServer")
//    public MongoClient reactiveMongoClient() {
//        int port = environment.getProperty("spring.data.mongodb.port", Integer.class);
//        return MongoClients.create(String.format("mongodb://localhost:%d", port));
//    }
//
//    @Override
//    protected String getDatabaseName() {
//        return environment.getProperty("spring.data.mongodb.database");
//    }
}
