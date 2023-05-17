package com.express.comment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "comment")
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    private String id;
    private String label;
    private List<Comment> children;
}
