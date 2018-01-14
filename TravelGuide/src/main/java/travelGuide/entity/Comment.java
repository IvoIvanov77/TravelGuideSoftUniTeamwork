package travelGuide.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity(name = "comments")
public class Comment {
    private Integer id;

    private String title;

    private String content;

    private User author;

    private Article article;

    public Comment() {
    }

    public Comment(String title, String content, User author, Article article) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.article = article;
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
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(nullable = false)
    public String getContent() {
        return this.content;
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

    @ManyToOne
    @NotNull
    @JoinColumn(nullable = false, name = "articleId")
    public Article getArticle() {
        return this.article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}
