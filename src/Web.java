import processing.core.PImage;

import java.util.List;

public class Web extends Entity {
    public static final String WEB_KEY = "web";

    public Web(String id, Point position, List<PImage> images){
        super(id,position,images);
    }
}