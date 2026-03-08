package com.lean.Task.Repository;

import com.lean.Task.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

//The reason we use an interface instead of a class is that Spring Boot follows the Data Access Object (DAO) pattern.
// When the application starts, Spring sees this interface and automatically generates a "Proxy" class behind the scenes that contains all the logic for save(), findAll(), findById(), and delete().
@Repository //@Repository is a Spring annotation that indicates this interface is a repository component, which is responsible for data access and manipulation.
            // It also allows Spring to automatically detect and manage it as a bean in the application context.
public interface TaskRepository extends MongoRepository<Task, String> {
}
