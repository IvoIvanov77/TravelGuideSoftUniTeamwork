package softuniBlog.utils;

import softuniBlog.entity.Image;

import java.io.File;
import java.util.Set;

import static softuniBlog.utils.Constants.IMAGE_DIRECTORY;
import static softuniBlog.utils.Constants.IMAGE_FOLDER_PATH;

public final class DeleteImage {
    private static void deleteImageFiles(String current) {
        String originalName = current.substring(IMAGE_FOLDER_PATH.length());
        File imageFile = new File(System.getProperty("user.dir") + IMAGE_DIRECTORY + originalName);
        if (imageFile.delete()) {
            System.out.println(imageFile.getName() + " is deleted!");
        } else {
            System.out.println("Delete operation is failed!");
        }
    }

    public static void deleteImagesFiles(Set<Image> images) {
        images.forEach(i -> DeleteImage.deleteImageFiles(i.getSmallImagePath()));
        images.forEach(i -> DeleteImage.deleteImageFiles(i.getBigImagePath()));
    }
}