package softuniBlog.bindingModel;


import softuniBlog.enums.Rating;

import javax.validation.constraints.NotNull;

public class ArticleBindingModel {

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private Integer destinationId;

    private Rating rating;

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

    public Rating getRating() {
        return this.rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }
}
