package com.lemick.demo.controller;

import com.lemick.demo.entity.BlogPost;
import com.lemick.demo.repository.BlogPostRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blogPosts")
public class BlogPostController {

    @Autowired
    BlogPostRepository blogPostRepository;

    @PostMapping
    @Transactional
    public BlogPost create(@Valid @RequestBody BlogPost blogPost) {
        return blogPostRepository.save(blogPost);
    }

}
