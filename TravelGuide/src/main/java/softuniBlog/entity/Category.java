package softuniBlog.entity;

import javax.persistence.*;
import java.util.*;

@Entity(name = "categories")
public class Category {

    private Integer id;

    private String name;

    private Set<Destination> destinations;

    private User author;

    public Category() {
    }

    public Category(String name, User author) {
        this.name = name;
        this.author = author;
        this.destinations = new HashSet<>();
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
    public Set<Destination> getDestinations() {
       /* Comparator<Destination> comp = (o1, o2) -> {
            int compare = Double.compare(o2.getStarRating(), o1.getStarRating());
            if (compare == 0) {
                return Integer.compare(o2.getId(), o1.getId());
            }
            return compare;
        };*/
//        this.destinations.sort(comp);
        return this.destinations;
    }

    public void setDestinations(Set<Destination> destinations) {
        this.destinations = destinations;
    }

  /*  @Transient
    public List<Destination> getUniqueDestinations() {
        List<Integer> ids = new ArrayList<>();
        List<Destination> filteredDestinations = new ArrayList<>();
        for (Destination destination : this.getDestinations()) {
            Integer targetId = destination.getId();
            if (!ids.contains(targetId)) {
                ids.add(targetId);
                filteredDestinations.add(destination);
            }
        }
        return filteredDestinations;
    }*/
}
