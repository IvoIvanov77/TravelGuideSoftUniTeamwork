package softuniBlog.bindingModel;

import org.springframework.web.bind.annotation.SessionAttributes;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@SessionAttributes
public class FilterBindingModel {

    @NotNull
    private List<Integer> categoryIds;

    @NotNull
    private List<Integer> authorIds;

    public FilterBindingModel() {
        this.clearData();
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<Integer> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(List<Integer> authorIds) {
        this.authorIds = authorIds;
    }

    public void clearData(){
        this.categoryIds = new ArrayList<>();
        this.authorIds = new ArrayList<>();
    }
}
