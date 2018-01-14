package travelGuide.bindingModel;

import javax.validation.constraints.NotNull;

public class SearchBindingModel {
    @NotNull
    private String title;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
