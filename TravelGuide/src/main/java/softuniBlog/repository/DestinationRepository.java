package softuniBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import softuniBlog.entity.Destination;

import java.util.List;

public interface DestinationRepository extends JpaRepository<Destination, Integer> {
    Destination findByName(String name);

    @Query("select d from Destination d order by d.starRating desc, d.id desc")
    List<Destination> findAllOrderedByRatingDesc();

    @Query("select d from Destination d where d.category.id=:catId")
    List<Destination> getUniqueDestinationsByCategoryId(@Param("catId") Integer catId);
}
