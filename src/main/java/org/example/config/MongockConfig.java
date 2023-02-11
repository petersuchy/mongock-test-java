package org.example.config;

import com.mongodb.reactivestreams.client.MongoClient;
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MongockConfig {

    @Value("${spring.data.mongodb.database}")
    String MONGODB_DATABASE_NAME;

    @Value("${mongock.migration-scan-package}")
    String MONGOCK_MIGRATION_PACKAGE;

    @Bean
    public MongockInitializingBeanRunner getBuilder(MongoClient reactiveMongoClient, ApplicationContext context) {

        return MongockSpringboot.builder()
                .setDriver(MongoReactiveDriver.withDefaultLock(reactiveMongoClient, MONGODB_DATABASE_NAME))
                .addMigrationScanPackage(MONGOCK_MIGRATION_PACKAGE)
                .setSpringContext(context)
                .setTransactionEnabled(true)
                .buildInitializingBeanRunner();
    }


}
