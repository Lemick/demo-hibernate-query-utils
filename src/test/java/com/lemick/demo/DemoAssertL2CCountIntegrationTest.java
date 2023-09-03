package com.lemick.demo;

import com.lemick.demo.entity.BlogPost;
import com.lemick.demo.entity.PostComment;
import com.lemick.demo.repository.BlogPostRepository;
import com.mickaelb.api.AssertHibernateL2CCount;
import com.mickaelb.integration.spring.HibernateAssertTestListener;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.transaction.support.TransactionTemplate;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestExecutionListeners(listeners = HibernateAssertTestListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DemoAssertL2CCountIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private TestRestTemplate restTemplate;

    @Autowired
    TransactionTemplate transactionTemplate;

    @PostConstruct
    public void initialize() {
        RestTemplateBuilder customRestBuilder = restTemplateBuilder.rootUri("http://localhost:" + port);
        this.restTemplate = new TestRestTemplate(customRestBuilder);
    }

    @Test
    @Order(0)
    @AssertHibernateL2CCount(misses = 1, puts = 1, hits = 1)
    void _create_one_post_and_read_it() {
        doInTransaction(() -> {
            BlogPost post_1 = new BlogPost("Blog post 1");
            post_1.addComment(new PostComment("Good article"));
            blogPostRepository.save(post_1);
        });

        doInTransaction(() -> {
            blogPostRepository.findById(1L); // 1 MISS + 1 PUT
        });

        doInTransaction(() -> {
            blogPostRepository.findById(1L); // 1 HIT
        });
    }

    @Test
    @AssertHibernateL2CCount(hits = 1)
    void _read_post_from_cache() {
        doInTransaction(() -> {
            blogPostRepository.findById(1L); // 1 HIT
        });
    }
    @Test
    @AssertHibernateL2CCount(hits = 1)
    void _read_post_from_cache_with_http() {
        restTemplate.getForObject("/blogPosts/1", BlogPost.class); // 1 HIT
    }

    void doInTransaction(Runnable runnable) {
        transactionTemplate.execute(status -> {
            runnable.run();
            return null;
        });
    }
}
