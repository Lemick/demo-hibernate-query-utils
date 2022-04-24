package com.lemick.demo.repository;

import com.lemick.demo.entity.PostComment;
import org.springframework.data.repository.CrudRepository;

public interface PostCommentRepository extends CrudRepository<PostComment, Long> {

}
