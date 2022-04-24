package com.lemick.demo.repository;

import com.lemick.demo.entity.BlogPost;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BlogPostRepository extends CrudRepository<BlogPost, Long> {

    @Query("SELECT p FROM BlogPost p JOIN FETCH p.postComments c")
    List<BlogPost> findBlogPostWithComments();
}
