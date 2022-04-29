package com.lemick.demo.controller;

import com.lemick.demo.dto.BlogPostDTO;
import com.lemick.demo.entity.BlogPost;
import com.lemick.demo.repository.BlogPostRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/blogPosts")
public class BlogPostController {

    public static final String BLOG_POST_ID = "blogPostId";

    @Autowired
    BlogPostRepository blogPostRepository;

    @GetMapping("/{" + BLOG_POST_ID + ":[0-9]+}")
    @Transactional
    public BlogPostDTO find(@PathVariable(name = BLOG_POST_ID) Long id) {
        Optional<BlogPost> blogPost = blogPostRepository.findById(id);
        if (blogPost.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return new BlogPostDTO(blogPost.get().getId(), blogPost.get().getTitle());
    }

    @PostMapping
    @Transactional
    public BlogPost create(@Valid @RequestBody BlogPost blogPost) {
        return blogPostRepository.save(blogPost);
    }

}
