package softuniBlog.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by George-Lenovo on 6/29/2017.
 */
@Entity
@Table(name = "marks")
public class Mark {
    private Integer id;
    private String event;
    private Destination destination;
    private Image image;
    private User author;
    private Double lat;
    private Double lng;
    private Boolean approved = false;

    public Mark() {
    }

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
    @JoinColumn(name = "imageId")
    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Column(name = "latitude", nullable = false)
    public Double getLat() {
        return this.lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    @Column(name = "longitude", nullable = false)
    public Double getLng() {
        return this.lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Column(name = "event", nullable = false)
    public String getEvent() {
        return this.event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Column(name = "is_approved", nullable = false)
    public Boolean getApproved() {
        return this.approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
}
