package softuniBlog.utils;

import org.apache.commons.io.FileUtils;
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

    /**
     * Resize and uplaod image of type MultipartFile on hdd and return path to it.
     *
     * @param width
     * @param height
     * @param file
     * @return path of uploaded image.
     */
    public static String upload(int width, int height, MultipartFile file) {
        String path = null;
        String imageDirectoryParam = System.getProperty("user.dir") + Constants.IMAGE_PATH;
        String folderPathParam = Constants.IMAGE_FOLDER_PATH;
        if (file != null) {
            String originalName = file.getOriginalFilename();
            File imageFile = new File(imageDirectoryParam, originalName);
            try {
//                file.transferTo(imageFile);
                FileUtils.writeByteArrayToFile(imageFile, file.getBytes());
                path = doUpload(imageDirectoryParam, folderPathParam, width, height, imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return path;
    }

    private static String doUpload(String imageDirectoryParam, String folderPathParam, int widthParam, int heightParam,
                                   File imageFile) {
        String finalName = resizeAndWriteImage(imageDirectoryParam, imageFile, widthParam, heightParam);
        deleteOriginalFile(imageFile);
        return folderPathParam + finalName;
    }

    private static void deleteOriginalFile(File imageFile) {
        if (!imageFile.delete()) {
            System.out.println("Delete operation is failed.");
        }
    }

    public static String resizeAndWriteImage(String imageDirectoryParam, File imageFile, int widthParam, int heightParam) {
        BufferedImage originalImage;
        String dest = null;
        try {
            System.out.println(imageFile.getCanonicalPath());
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
