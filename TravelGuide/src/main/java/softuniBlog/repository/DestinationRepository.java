package softuniBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import softuniBlog.entity.Destination;

import java.util.List;

public interface DestinationRepository extends JpaRepository<Destination, Integer> {
    @Query("select d from Destination d order by d.starRating desc, d.id desc")
    List<Destination> findAllOrderedByRatingDescIdDesc();

    @Query("select d from Destination d order by d.id desc")
    List<Destination> findAllByIdDesc();

    @Query("select min(d.id) from Destination d")
    int getMinId();

    @Query("select max(d.id) from Destination d")
    int getMaxId();
}
