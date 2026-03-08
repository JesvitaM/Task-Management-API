package com.lean.Task.model;

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
    private String title;
    private String description;
    private boolean completed;
}
