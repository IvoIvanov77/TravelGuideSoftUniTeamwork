package softuniBlog.entity;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "destinations")
public class Destination {

    private Integer id;

    private String name;

    private String review;

    private Double starRating;

    private User author;

    private Set<Article> articles;

    public Destination(String name, String review, Double starRating, User author, Set<Article> articles) {
        this.name = name;
        this.review = review;
        this.starRating = starRating;
        this.author = author;
        this.articles = articles;
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

    @Column(nullable = false)
    public String getReview() {
        return this.review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    @Column(nullable = false)
    public Double getStarRating() {
        return this.starRating;
    }

    public void setStarRating(Double starRating) {
        this.starRating = starRating;
    }

    @ManyToOne
    @JoinColumn(nullable = false, name = "authorId")
    public User getAuthor() {
        return this.author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

//    TODO add the relationship between article and destination
//    @OneToMany
//    @JoinColumn(nullable = false, name = "id")
    public Set<Article> getArticles() {
        return this.articles;
    }

    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }
}
