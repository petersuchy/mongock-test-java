package org.example.repository;


import org.example.App;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import org.example.repository.entity.Client;

@Repository(App.CLIENTS_COLLECTION_NAME)
public interface ClientRepository extends ReactiveMongoRepository<Client, String> {

}
