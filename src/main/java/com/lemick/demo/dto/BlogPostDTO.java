package com.lemick.demo.dto;

import com.lemick.demo.entity.PostComment;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

public record BlogPostDTO(Long id, String title) {

}
