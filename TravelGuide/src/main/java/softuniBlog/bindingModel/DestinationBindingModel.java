package softuniBlog.bindingModel;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by George-Lenovo on 6/29/2017.
 */
public class DestinationBindingModel {

    @NotNull
    private String name;
    @NotNull
    private String review;
    @NotNull
    private Integer categoryId;
    @NotNull
    private Double price;

    @NotNull
    private MultipartFile picture;

    private List<MultipartFile> pictures;

    public Integer getCategoryId() {
        return categoryId;
    }

    public MultipartFile getPicture() {
        return picture;
    }

    public List<MultipartFile> getPictures() {
        return pictures;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }

    public void setPictures(List<MultipartFile> pictures) {
        this.pictures = pictures;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
