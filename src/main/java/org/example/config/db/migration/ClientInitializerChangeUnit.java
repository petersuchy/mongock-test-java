package org.example.config.db.migration;


import com.mongodb.reactivestreams.client.MongoCollection;
import io.mongock.api.annotations.*;
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync;
import io.mongock.driver.mongodb.reactive.util.SubscriberSync;
import org.bson.Document;
import org.example.App;
import org.example.repository.ClientRepository;
import org.example.repository.entity.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.stream.IntStream;


@ChangeUnit(id = "client-initializer", order = "1", author = "mongock")
public class ClientInitializerChangeUnit {

    public final static int INITIAL_CLIENTS = 10;
    private static final Logger logger = LoggerFactory.getLogger(ClientInitializerChangeUnit.class);

    SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();

    @BeforeExecution
    public void beforeExecution(ReactiveMongoTemplate mongoTemplate) {
        SubscriberSync<MongoCollection<Document>> subscriber = new MongoSubscriberSync<>();

        mongoTemplate.createCollection(App.CLIENTS_COLLECTION_NAME).subscribe(subscriber);
        subscriber.await();
        logger.info("ClientInitializerChangeLog.beforeExecution: Finished");
    }

    @RollbackBeforeExecution
    public void rollbackBeforeExecution(ReactiveMongoTemplate mongoTemplate) {
        mongoTemplate.dropCollection(App.CLIENTS_COLLECTION_NAME).subscribe(subscriber);
        subscriber.await();
        logger.info("ClientInitializerChangeLog.rollbackBeforeExecution: Finished");
    }

    @Execution
    public void execution(ClientRepository clientRepository) {
        SubscriberSync<Client> subscriber = new MongoSubscriberSync<>();
        Flux.fromIterable(
                        IntStream.range(0, INITIAL_CLIENTS)
                                .mapToObj(ClientInitializerChangeUnit::getClient)
                                .toList()
                )
                .flatMap(clientRepository::save)
                .subscribe(subscriber);
        subscriber.get().forEach(result -> logger.info("Client inserted successfully: " + result.toString()));
        logger.info("ClientInitializerChangeLog.execution: Finished");

    }

    @RollbackExecution
    public void rollbackExecution(ClientRepository clientRepository) {
        clientRepository.deleteAll().log().subscribe(subscriber);
        subscriber.await();
        logger.info("ClientInitializerChangeLog.rollbackExecution: Finished");
    }

    private static Client getClient(int i) {
        return new Client("name-" + i, "email-" + i, "phone" + i, "country" + i);
    }
}
