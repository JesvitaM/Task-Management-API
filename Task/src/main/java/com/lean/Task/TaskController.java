package com.lean.Task;

import com.lean.Task.Repository.TaskRepository;
import com.lean.Task.model.Task;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// CRUD operations for Task entity
// Create ,Read, Update, Delete

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired // This annotation tells Spring to automatically inject an instance of TaskRepository into this controller.
             // It allows us to use the repository's methods to interact with the database without having to manually instantiate it.
    private TaskRepository taskRepository;

    //Get all tasks from MongoDB
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Save a new task to MongoDB
    @PostMapping
    public Task createTask(@Valid @RequestBody Task task) {
        return taskRepository.save(task);
    }

    //Get task by id

        //There are 2 ways get it, one is to return Optional<Task> and the other is to return Task directly where we use .orElse(null) to return null if the task is not found.
        //The first way is more flexible because it allows you to handle the case where the task with the given id does not exist (i.e., it returns an empty Optional).
        //  The second way is simpler but less flexible because it returns null if the task does not exist, which can lead to NullPointerExceptions if not handled properly.
    @GetMapping("/{id}")
    public Optional<Task> getTaskById(@PathVariable String id) {
        return taskRepository.findById(id);
    }
 //To Run: GET http://localhost:8080/api/tasks/69ad83012c320474a29f7080
    // Here 69ad83012c320474a29f7080 is the {{id}} of the task we want to retrieve.
    // You can replace it with any valid task id from your MongoDB collection.

//    @GetMapping("/{id}")
//    public Task getTaskById(@PathVariable String id) {
//        return taskRepository.findById(id).orElse(null);
//    }

    @GetMapping("/status/{isCompleted}")
    public List<Task> getTasksByStatus(@PathVariable boolean isCompleted) {
        return taskRepository.findByCompleted(isCompleted);
    }

    //Delete task by id
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        taskRepository.deleteById(id);
    }

//  Update Operation
//  We have two choices for updates:
//    PUT: This is used to "Replace" the entire object. You send the whole task (ID, title, description, and status).
//    PATCH: This is used for "Partial" updates. You might only send the completed status.

//    Finds the existing task, update its fields with the data from the request, and save it back to MongoDB.
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable String id, @Valid @RequestBody Task updatedTask) {
        return taskRepository.findById(id)
                .map(existingtask -> {
                    existingtask.setTitle(updatedTask.getTitle());
                    existingtask.setDescription(updatedTask.getDescription());
                    existingtask.setCompleted(updatedTask.isCompleted());
                    return taskRepository.save(existingtask);
                })
                .orElseGet(() -> {
                    updatedTask.setId(id);
                    return taskRepository.save(updatedTask);
                });
    }

    @PatchMapping("/{id}")
    public Task partialUpdateTask(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        return taskRepository.findById(id).map(task -> {
            // We check the map for specific keys and update the object
            updates.forEach((key, value) -> {
                switch (key) {
                    case "title" -> task.setTitle((String) value);
                    case "description" -> task.setDescription((String) value);
                    case "completed" -> task.setCompleted((boolean) value);
                }
            });
            return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task not found"));
    }
}
