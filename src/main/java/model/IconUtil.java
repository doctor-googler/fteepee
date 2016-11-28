package model;

import javafx.scene.image.Image;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 11/28/2016.
 */
public class IconUtil {
    private Map<String,Image> icons = new HashMap<>();
    private String[] extensions = {
            "AVI","ADOBE","DOC","DOCX","HTML","CSS",
            "JPG","MP4","MP3","PNG",
            "P","PPT","PPTX","PS","PSD","XLSX",
            "XLS","FOLDER","GENERIC"
    };
    public IconUtil() {
        try {
            for (String name : extensions) {
                URI iconUri = IconUtil.class.getResource(String.format("/icons/%s.png", name)).toURI();
                Image img = new Image(iconUri.toString());
                icons.put(name, img);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Image getFileIcon(String fileName) {
        if (fileName == null || fileName.length() == 0) {
            return icons.get("GENERIC");
        }
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex <= 0 || dotIndex == fileName.length()) {
            return icons.get("GENERIC");
        }

        String extension = fileName.substring(fileName.lastIndexOf(".")+1).toUpperCase();
        if (icons.containsKey(extension)) {
            return icons.get(extension);
        }

        return icons.get("GENERIC");
    }

    public Image getFolderIcon() {
        return icons.get("FOLDER");
    }
}
