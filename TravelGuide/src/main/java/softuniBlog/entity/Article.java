package softuniBlog.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "articles")
public class Article {

    private Integer id;

    private String title;

    private String content;

    private User author;

    private Destination destination;

    private Set<Comment> comments;

    private Set<Vote> votes;

    public Article(String title, String content, User author, Destination destination, Set<Comment> comments) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.destination = destination;
        this.comments = comments;
        this.votes = new HashSet<>();
    }

    public Article() {
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(columnDefinition = "text", nullable = false)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @ManyToOne
    @JoinColumn(nullable = false, name = "authorId")
    public User getAuthor() {
        return this.author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Transient
    public String getSummary() {
        return this.getContent().substring(0, this.getContent().length() / 2) + "...";
    }

    @OneToMany(mappedBy = "article")
    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @OneToMany(mappedBy = "article")
    public Set<Vote> getVotes() {
        return this.votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }
}
