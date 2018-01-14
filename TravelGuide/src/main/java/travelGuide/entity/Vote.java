package travelGuide.entity;

import javax.persistence.*;

@Entity(name = "users_votes")
public class Vote {
    private Integer id;
    private User user;
    private Article article;
    private Integer vote = 0;

    public Vote() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = User.class)
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(targetEntity = Article.class)
    public Article getArticle() {
        return this.article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Integer getVote() {
        return this.vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }
}
