package softuniBlog.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity(name = "categories")
public class Category {

    private Integer id;

    private String name;

    private List<Destination> destinations;

    private User author;

    public Category() {
    }

    public Category(String name, User author) {
        this.name = name;
        this.author = author;
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

    @ManyToOne
    @JoinColumn(nullable = false, name = "authorId")
    public User getAuthor() {
        return this.author;
    }

    public void setAuthor(User author) {
        this.author = author;
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
        Comparator<Destination> comp = Comparator.comparing(Destination::getId);
        this.destinations.sort(comp);
        return this.destinations;
    }

    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }
}
