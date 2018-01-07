package softuniBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import softuniBlog.entity.Mark;

/**
 * Created by George-Lenovo on 6/29/2017.
 */
public interface MarkRepository extends JpaRepository<Mark, Integer> {

    @Query("select min(m.id) from marks m")
    int getMinId();

    @Query("select max(m.id) from marks m")
    int getMaxId();
}
