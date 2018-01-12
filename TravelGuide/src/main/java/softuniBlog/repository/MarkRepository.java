package softuniBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import softuniBlog.entity.Mark;

import java.util.List;

/**
 * Created by George-Lenovo on 6/29/2017.
 */
public interface MarkRepository extends JpaRepository<Mark, Integer> {
    @Query("select m from Mark m where m.destination.id=:destId order by m.id desc")
    List<Mark> findAllMarksOrderByIdDesc(@Param("destId") Integer destinationId);

    @Query("select min(m.id) from Mark m")
    int getMinId();

    @Query("select max(m.id) from Mark m")
    int getMaxId();
}
