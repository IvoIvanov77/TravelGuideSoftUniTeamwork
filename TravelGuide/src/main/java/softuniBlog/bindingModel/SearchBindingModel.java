package softuniBlog.bindingModel;

public class SearchBindingModel {

    public SearchBindingModel(){

    }

    private String title;

    public SearchBindingModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
