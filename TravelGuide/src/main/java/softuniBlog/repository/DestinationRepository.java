package softuniBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuniBlog.entity.Destination;

public interface DestinationRepository extends JpaRepository<Destination, Integer> {
    Destination findByName (String name);
}
