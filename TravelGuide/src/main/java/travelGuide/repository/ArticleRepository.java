package travelGuide.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import travelGuide.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    Article findByTitle(String title);
}
