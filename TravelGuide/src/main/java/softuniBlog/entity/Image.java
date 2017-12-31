package softuniBlog.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "image")
public class Image {
    private Long id;
    private String imagePath;
    private Destination destination;

    public Image() {
    }

    public Image(String path, Destination destination) {
        this.destination = destination;
        this.imagePath = path;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "text", nullable = false)
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @ManyToOne
    @NotNull
    @JoinColumn(nullable = false, name = "destinationId")
    public Destination getDestination() {
        return destination;
    }

    public void setAlbumHolder(Destination destination) {
        this.destination = destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    @Transient
    public String getName() {
        return this.imagePath.substring(14);
    }
}