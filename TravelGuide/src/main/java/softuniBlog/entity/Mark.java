package softuniBlog.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by George-Lenovo on 6/29/2017.
 */
@Entity(name = "marks")
public class Mark {
    private Integer id;
    private Destination destination;
    private Image image;
    private User author;

    public Mark(Destination destination, Image image, User author) {
        this.destination = destination;
        this.image = image;
        this.author = author;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn(nullable = false, name = "authorId")
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @ManyToOne
    @NotNull
    @JoinColumn(nullable = false, name = "destinationId")
    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "imageId", nullable = false)
    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
