package softuniBlog.entity;

import softuniBlog.utils.RandomNumber;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.DoubleSummaryStatistics;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.Set;

@Entity
@Table(name = "destinations")
public class Destination {

    private Integer id;

    private String name;

    private String review;

    private Double starRating;

    private User author;

    private Set<Article> articles;

    private Set<Image> images;

    private Set<Mark> marks;

    private Category category;

    private Double price;

    public Destination() {
    }

    public Destination(String name, String review, User author, Category category, Double price) {
        this.name = name;
        this.review = review;
        this.starRating = 0D;
        this.author = author;
        this.category = category;
        this.price = price;
        this.articles = new HashSet<>();
        this.marks = new HashSet<>();
        this.images = new HashSet<>();
    }

    @OneToMany(mappedBy = "destination")
    public Set<Mark> getMarks() {
        return this.marks;
    }

    public void setMarks(Set<Mark> marks) {
        this.marks = marks;
    }

    @Transient
    public Image getRandomImage() {
        int size = this.images.size();
        int number = RandomNumber.getRandomNumber(0, size);

        int i = 0;
        for (Image img : this.images) {
            if (i == number)
                return img;
            i++;
        }

        return null;
    }

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<Image> getImages() {
        return this.images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    @ManyToOne
    @NotNull
    @JoinColumn(nullable = false, name = "categoryId")
    public Category getCategory() {
        return this.category;
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
        return this.calculateRating();
    }

    public void setStarRating(Double starRating) {
        this.starRating = starRating;
    }

    @ManyToOne()
    @JoinColumn(nullable = false, name = "authorId")
    public User getAuthor() {
        return this.author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @OneToMany(mappedBy = "destination", cascade = CascadeType.REMOVE)
    public Set<Article> getArticles() {
        return this.articles;
    }

    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }

    @Column(nullable = false)
    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void addImages(Set<Image> images) {
        if (this.images != null) {
            this.images.addAll(images);
        }
    }

    private Double calculateRating() {
        DoubleSummaryStatistics stats = this.articles.stream()
                    .mapToDouble(Article::getStarRating)
                    .summaryStatistics();
        return stats.getAverage();
    }

}
