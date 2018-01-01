package softuniBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import softuniBlog.entity.Destination;

import java.util.List;

public interface DestinationRepository extends JpaRepository<Destination, Integer> {
    Destination findByName(String name);

    @Query("select d from Destination d order by d.starRating desc, d.id asc")
    List<Destination> findAllOrderedByRatingDesc();
}
