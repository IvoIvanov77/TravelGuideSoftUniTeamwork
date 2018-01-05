package softuniBlog.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "image")
public class Image {
    private Long id;
    private String smallImagePath;
    private String bigImagePath;
    private Destination destination;
    private Mark mark;

    public Image() {
    }

    public Image(String smallImagePath, Destination destination) {
        this.destination = destination;
        this.smallImagePath = smallImagePath;
        this.bigImagePath = null;
    }
    public Image(String smallImagePath, String bigImagePath, Destination destination) {
        this.destination = destination;
        this.smallImagePath = smallImagePath;
        this.bigImagePath = bigImagePath;
    }

    @OneToOne(mappedBy = "image")
    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "bigImagePath", columnDefinition = "text", nullable = false)
    public String getBigImagePath() {
        return bigImagePath;
    }

    public void setBigImagePath(String bigImagePath) {
        this.bigImagePath = bigImagePath;
    }

    @Column(name = "smallImagePath", columnDefinition = "text", nullable = false)
    public String getSmallImagePath() {
        return smallImagePath;
    }

    public void setSmallImagePath(String smallImagePath) {
        this.smallImagePath = smallImagePath;
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

    /*@Transient
    public String getName() {
        return this.smallImagePath.substring(IMAGE_FOLDER_PATH.length());
    }*/
}