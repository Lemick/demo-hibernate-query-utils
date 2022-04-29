package com.lemick.demo;

import com.lemick.demo.dto.BlogPostDTO;
import com.lemick.demo.entity.BlogPost;
import com.lemick.demo.entity.PostComment;
import com.lemick.demo.repository.BlogPostRepository;
import com.lemick.demo.repository.PostCommentRepository;
import com.mickaelb.api.AssertHibernateSQLCount;
import com.mickaelb.integration.spring.HibernateAssertTestListener;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestExecutionListeners(listeners = HibernateAssertTestListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DemoAssertSQLCountIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private TestRestTemplate restTemplate;

    @PostConstruct
    public void initialize() {
        RestTemplateBuilder customRestBuilder = restTemplateBuilder.rootUri("http://localhost:" + port);
        this.restTemplate = new TestRestTemplate(customRestBuilder);
    }

    @Test
    @Transactional
    @Commit
    @Order(0)
    @AssertHibernateSQLCount(inserts = 6)
    void create_three_blog_posts() {
        BlogPost post_1 = new BlogPost("Blog post 1");
        post_1.addComment(new PostComment("Good article"));
        blogPostRepository.save(post_1);

        BlogPost post_2 = new BlogPost("Blog post 2");
        post_2.addComment(new PostComment("Nice"));
        blogPostRepository.save(post_2);

        BlogPost post_3 = new BlogPost("Blog post 3");
        post_3.addComment(new PostComment("Coooool"));
        blogPostRepository.save(post_3);
    }

    @Test
    @Transactional
    @AssertHibernateSQLCount(selects = 1)  // <= This will warn you if you're triggering N+1 SELECT
    void fetch_post_and_comments_with_one_select() {
        blogPostRepository.findBlogPostWithComments().forEach(blogPost ->
                assertEquals(1, blogPost.getPostComments().size(), "all blog posts have one comment")
        );
    }

    /**
     * Will Trigger 1 SELECT &  2 DELETE
     * No select for the blogPost because it's present in the cache, but one select for the child comments because of the cascade DELETE
     */
    @Test
    @Transactional
    @AssertHibernateSQLCount(selects = 1, deletes = 2)
    void delete_one_entity() {
        blogPostRepository.deleteById(1L);
    }

    @Test
    @AssertHibernateSQLCount(inserts = 1)
    void create_one_entity_from_endpoint() {
        BlogPost requestBody = new BlogPost("My new blog post");

        BlogPostDTO responseBody = restTemplate.postForObject("/blogPosts", requestBody, BlogPostDTO.class);

        assertEquals("My new blog post", responseBody.title(), "The blog post created is returned");
    }
}
