package softuniBlog.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "destinations")
public class Destination {

    private Integer id;

    private String name;

    private String review;

    private Double starRating;

    private User author;

    private Set<Article> articles;

    private Category category;

    private Double price;

    public Destination() {
    }

    public Destination(String name, String review, User author, Category category, Double price) {
        this.name = name;
        this.review = review;
        this.starRating = 0D;
        this.author = author;
        this.articles = new HashSet<>();
        this.category = category;
        this.price = price;
    }

    @ManyToOne
    @NotNull
    @JoinColumn(nullable = false, name = "categoryId")
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category destination) {
        this.category = destination;
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

    @OneToMany(mappedBy = "destination")
    public Set<Article> getArticles() {
        return this.articles;
    }

    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }
    @Column(nullable = false)
    public Double getPrice(){
        return this.price;
    }

    public  void setPrice(Double price){
        this.price = price;
    }
}
