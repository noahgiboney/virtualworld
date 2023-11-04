import java.util.*;

import processing.core.PApplet;
import processing.core.PImage;

public final class ImageStore {
    private final Map<String, List<PImage>> images;
    private final List<PImage> defaultImages;

    public ImageStore(PImage defaultImage) {
        this.images = new HashMap<>();
        defaultImages = new LinkedList<>();
        defaultImages.add(defaultImage);
    }
    public void loadImages(Scanner in, PApplet screen, VirtualWorld virtualWorld) {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                virtualWorld.processImageLine(images(), in.nextLine(), screen);
            } catch (NumberFormatException e) {
                System.out.printf("Image format error on line %d\n", lineNumber);
            }
            lineNumber++;
        }
    }
    public Map<String, List<PImage>> images(){return this.images;}
    public List<PImage> getImageList(String key) {
        return this.images.getOrDefault(key, this.defaultImages);
    }
}
