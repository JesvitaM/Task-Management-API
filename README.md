# Task-Management-API
A RESTful backend service built with Spring Boot and MongoDB to manage daily tasks. This project was developed as part of a guided learning sprint to master CRUD operations and NoSQL database integration.

## Project Architecture
The project follows a standard layered architecture:
- Model: Task.java - Defines the data structure.
- Repository: TaskRepository.java - Handles database communication.
- Controller: TaskController.java - Manages REST endpoints.

## API Endpoints
We have implemented the following routes:

| Method    | Endpoint | Description |
| -------- | ------- | ------- |
| GET  | /api/tasks	| Retrieve all tasks |
| POST | /api/tasks	| Create a new task |
| PUT	| /api/tasks/{id}	| Replace an entire task |
| PATCH	| /api/tasks/{id}	| Partially update a task (e.g., toggle "completed") |
| DELETE |/api/tasks/{id}	| Remove a task |

The application connects to MongoDB using the following property in src/main/resources/application.properties: ``spring.data.mongodb.uri=mongodb://localhost:27017/taskdb``
