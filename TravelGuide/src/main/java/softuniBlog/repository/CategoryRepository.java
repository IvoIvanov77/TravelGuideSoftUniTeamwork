package softuniBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import softuniBlog.entity.Category;
import softuniBlog.entity.Destination;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
