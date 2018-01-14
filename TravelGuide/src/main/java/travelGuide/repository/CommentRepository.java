package travelGuide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travelGuide.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
