package softuniBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuniBlog.entity.Image;

/**
 * Created by George-Lenovo on 6/29/2017.
 */
public interface ImageRepository extends JpaRepository<Image, Integer> {

}
