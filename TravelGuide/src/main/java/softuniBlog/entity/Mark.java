package softuniBlog.entity;

import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    public Mark(Destination destination, Image image) {
        this.destination = destination;
        this.image = image;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @OneToOne
    @JoinColumn(name = "imageId", nullable = false)
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
