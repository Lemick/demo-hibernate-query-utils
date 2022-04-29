package com.lemick.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "blogPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> postComments;

    private String title;

    public BlogPost(String title) {
        this.postComments = new ArrayList<>();
        this.title = title;
    }

    public void addComment(PostComment postComment) {
        postComment.setBlogPost(this);
        this.postComments.add(postComment);
    }
}
