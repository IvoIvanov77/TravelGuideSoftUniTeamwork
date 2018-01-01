package softuniBlog.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "categories")
public class Category {

    private Integer id;

    private String name;

    private List<Destination> destinations;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
        this.destinations = new ArrayList<>();
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

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    public List<Destination> getDestinations() {
        return this.destinations;
    }

    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }
}
