package travelGuide.bindingModel;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * Created by George-Lenovo on 6/29/2017.
 */
public class MarkBindingModel {
    @NotNull
    private Integer destinationId;
    @NotNull
    private MultipartFile markPicture;

    public Integer getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }

    public MultipartFile getMarkPicture() {
        return markPicture;
    }

    public void setMarkPicture(MultipartFile markPicture) {
        this.markPicture = markPicture;
    }
}
