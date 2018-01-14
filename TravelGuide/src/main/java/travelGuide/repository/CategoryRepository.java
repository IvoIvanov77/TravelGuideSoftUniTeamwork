package travelGuide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travelGuide.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
