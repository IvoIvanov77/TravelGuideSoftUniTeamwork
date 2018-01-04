package softuniBlog.bindingModel;


import javax.validation.constraints.NotNull;

public class ArticleBindingModel {

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private Integer destinationId;

    private Integer vote;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }

    public Integer getVote() {
        return this.vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }
}
