package softuniBlog.bindingModel;

/**
 * Created by George-Lenovo on 6/29/2017.
 */
public class DestinationBindingModel {

    private String name;
    private String review;
    private Integer categoryId;
    private Double price;

    public Integer getCategoryId() {
        return categoryId;
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
