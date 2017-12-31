package softuniBlog.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.imgscalr.Scalr;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by George-Lenovo on 6/29/2017.
 */
public final class UploadImage {

    public static String upload(int widthParam, int heightParam, MultipartFile file) {
        String path = null;
        String imageDirectoryParam = System.getProperty("user.dir") + Constants.IMAGE_PATH;
        String folderPathParam = Constants.IMAGE_FOLDER_PATH;
        if (file != null) {
            String originalName = file.getOriginalFilename();
            File imageFile = new File(imageDirectoryParam, originalName);
            try {
                file.transferTo(imageFile);
                path = doUploadAndDelete(imageDirectoryParam, folderPathParam, widthParam, heightParam, true, imageFile);
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }

        return path;
    }

    private static String doUploadAndDelete(String imageDirectoryParam, String folderPathParam, int widthParam, int heightParam,
                                            boolean deleteOriginalFile, File imageFile) {
        String finalName = resizeAndWriteImage(imageDirectoryParam, imageFile, widthParam, heightParam);
        if (deleteOriginalFile) {
            deleteOriginalFile(imageFile);
        }
        return folderPathParam + finalName;
    }

    private static void deleteOriginalFile(File imageFile) {
        if (!imageFile.delete()) {
            System.out.println("Delete operation is failed.");
        }
    }

    private static String resizeAndWriteImage(String imageDirectoryParam, File imageFile, int widthParam, int heightParam) {
        BufferedImage originalImage;
        String dest = null;
        try {
            originalImage = ImageIO.read(imageFile);
            BufferedImage resizeImagePng = Scalr.resize(originalImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, widthParam, heightParam);
            String name;

            do {
                name = generateUniqueFileName();
            }
            while (new File(name).exists());

            String extension = "";
            String fileName = imageFile.getName();

            int i = fileName.lastIndexOf('.');
            if (i > 0) {
                extension = fileName.substring(i);
            }

            dest = name + extension;
            ImageIO.write(resizeImagePng, "png", new File(imageDirectoryParam, dest));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dest;
    }

    private static String generateUniqueFileName() {
        String filename = "";
        long millis = System.currentTimeMillis();
        String datetime = new Date().toString()/*.toGMTString()*/;
        datetime = datetime.replace(" ", "");
        datetime = datetime.replace(":", "");
        String rndchars = RandomStringUtils.randomAlphanumeric(16);
        filename = rndchars + "_" + datetime + "_" + millis;
        return filename;
    }
}
