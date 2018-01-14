package travelGuide.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import travelGuide.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
}