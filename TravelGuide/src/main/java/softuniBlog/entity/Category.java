package softuniBlog.entity;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "categories")
public class Category {

    private Integer id;

    private String name;

    private Set<Destination> destinations;

    public Category(Integer id, String name, Set<Destination> destinations) {
        this.id = id;
        this.name = name;
        this.destinations = destinations;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "category")
    public Set<Destination> getDestinations() {
        return this.destinations;
    }

    public void setDestinations(Set<Destination> destinations) {
        this.destinations = destinations;
    }
}
