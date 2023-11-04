import processing.core.PImage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class Dude extends ActionEntity implements Transform, MoveTo{

    public static final String DUDE_KEY = "dude";
    public static final int DUDE_ACTION_PERIOD = 0;
    public static final int DUDE_ANIMATION_PERIOD = 1;
    public static final int DUDE_LIMIT = 2;
    public static final int DUDE_NUM_PROPERTIES = 3;

    public Dude(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod){
        super(id, position, images, animationPeriod, actionPeriod);
    }

    @Override
    public String getKey(){
        return DUDE_KEY;
    }

}
