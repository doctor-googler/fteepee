package model;

import javafx.scene.image.Image;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class IconUtil {
    private static final String[] extensions = {"folder", "generic"};
    private final Map<String, Image> icons = new HashMap<>();

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
            return icons.get("generic");
        }
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex <= 0 || dotIndex == fileName.length()) {
            return icons.get("generic");
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (icons.containsKey(extension)) {
            return icons.get(extension);
        }

        return icons.get("generic");
    }

    public Image getFolderIcon() {
        return icons.get("folder");
    }
}
