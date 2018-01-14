package travelGuide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travelGuide.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}