package com.lean.Task.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//@Document // This annotation indicates that this class will be stored in a MongoDB collection
          // tells Spring that this class should be saved as a document.
//By default, Spring will name the collection after your class (e.g., task),
// but you can also specify a name like @Document(collection = "tasks").
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "tasks")
public class Task {
    @Id
    private String id;

    @NotBlank(message = "Title is mandatory Bro")// the Title cannot be empty
    @Size(max = 100, message = "Title is too long") // the Title cannot exceed 100 characters and if it does, it will return the message "Title is too long"
    private String title;

    private String description;
    private boolean completed;
}
