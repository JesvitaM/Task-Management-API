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

## Defensive Coding
n a microservice, if another service (or a user) sends an "empty" task, we don't want that hitting our database. It wastes resources and creates "data junk." We use Jakarta Validation to define rules directly on our Model.

To get started, we need to add the validation "starter" to your `build.gradle` file.
add the following line to the dependencies block:
```groovy
implementation 'org.springframework.boot:spring-boot-starter-validation'
```    
### Validation 
#### Add Rules to the Task Model
We use annotations like `@NotBlank` (the string cannot be empty) and `@Size` (limit the length).
to tell spring what a valid task looks like. 
If a request doesn't meet these rules, Spring will automatically return a 400 Bad Request response with details about the validation errors.

In Spring Boot, when you use @RequestBody, the framework simply converts the incoming JSON into a Java object. 
However, it doesn't automatically check if that object follows the rules (like @NotBlank) we set in the model. 
By adding `@Valid`, you tell Spring: "Before you let this request reach my code, check the rules in the `Task` class.
```
@PostMapping
public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
    .......
}
```
For PUT and PATCH 
1. PUT: We need `@Valid`. 
        Since a PUT request is supposed to send the entire object to replace the old one, 
        we must ensure the new version doesn't have a blank title or invalid data.
2. PATCH: We only want to validate the fields that are being updated. 
   - For example, if we're only updating the "completed" status, we don't need to validate the "description" field. 

### Global Exception Handling
To handle validation errors gracefully, we can create a global exception handler using `@RestControllerAdvice`.
So in GlobalExceptionHandler.java,
The annottation @RestControllerAdvice allows us to define a centralized place to handle exceptions across all controllers.
Its a speciakized version of @Component that is used to handle exceptions in RESTful web services.
It tells Spring: "Listen to all Controllers in the app. If any of them throw an exception, check this class for a matching `@ExceptionHandler`".

### Custom Queries
A way to make our repository smarter. Instead of bringing all tasks into Java and filtering them with a for loop, we ask MongoDB to do it.

- **Updating the Repository**:
    In `TaskRepository`, we can add a method to find tasks based on whether they are finished or not:
```
public interface TaskRepository extends MongoRepository<Task, String> {
    // Spring generates the logic for this automatically!
    List<Task> findByCompleted(boolean completed);
}
```
**Parsing**: Spring looks at the method name (e.g., findByCompleted).

**Mapping**: It breaks the name into parts: `find` (the action), `By` (the delimiter), and `Completed` (the property in your Task class).

**Translation**: It translates this into a native MongoDB query: `db.tasks.find({ "completed": true })`.

**Execution**: When you call that method, the proxy executes the query and maps the resulting BSON documents back into Java Task objects.

- **The controller Mapping**: 
To use this, we need an endpoint. 
Usually, we use `@GetMapping` with a PathVariable so the user can decide what status they want to see (e.g., /api/tasks/status/true).
````
@GetMapping("/status/{isCompleted}")
public List<Task> getTasksByStatus(@PathVariable boolean isCompleted) {
    return taskRepository.findByCompleted(isCompleted);
}
````

##  Security
### The Security Filter Chain 

In Spring boot, security doent happen inside your Controller. Instead, it happens in a Filter Chain that sits in front of your controllers.
When a request comes in, it must pass through various "security checks" (filters) before Spring allows it to reach your code.

The moment you add this dependency, Spring Boot's "auto-configuration" kicks in. It's like a pre-installed security system that activates the moment you plug it in.

1. _Form Login_: All your web endpoints (like /api/tasks) are now protected. If you try to access them in a browser, you'll likely be redirected to a default login page.
2. _Default User_: Spring creates a default user with the username `user`.
3. _Generated Password_: It generates a unique password every time the application starts (In console). You can find this in your Console/Terminal log—look for a line that says:
`Using generated security password: XXXX-XXXX-XXXX`

Or could define in `application.properties` file:
```spring.security.user.name=admin
spring.security.user.password=admin123
```

_Real World_ problem: In a Microservices architecture, you don't want a browser-based login form for every service.
If "Service A" needs to ask "Service B" for a task list, it can't "type" into a username box.

### The Security Configuration Class
To customize this behavior, we create our own Security Configuration class that implements `SecurityFilterChain`.
-- `SecurityConfig.java`

Why did we do this?
1. **CSRF Disable**: Cross-Site Request Forgery (CSRF) protection is great for websites with forms, but for Stateless APIs (like ours), it usually gets in the way. We disable it so tools like Postman or other Microservices can talk to us easily.
2. **Granular Rules**: Notice how we made GET requests public? This allows anyone to see the tasks, but if someone tries to POST a new task or DELETE one, the Bouncer will stop them.
3. **HTTP Basic**: This replaces the "Login Page" with a standard browser popup. It's much easier for testing and for service-to-service communication.

Think of it like a museum:
- The Gallery (GET requests): Anyone can walk in and look at the paintings. We set this with .requestMatchers(HttpMethod.GET, "/api/tasks/**").permitAll().
- The Back Office (POST, PUT, DELETE requests): You need a keycard to get in here. We set this with .anyRequest().authenticated().

To get past the 401 error:
Add an Authorization header in IntelliJ
````
POST http://localhost:8080/api/tasks
Content-Type: application/json
Authorization: Basic admin password123

{
  "title": "Learn Spring Security",
  "description": "Mastering the HTTP Client",
  "completed": false
}
````