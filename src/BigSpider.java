import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class BigSpider extends Spider {

    public static final String BIG_SPIDER_KEY = "big_spider";
    public static final int BIG_SPIDER_ANIMATION_PERIOD = 0;
    public static final int BIG_SPIDER_ACTION_PERIOD = 1;
    private static final PathingStrategy BIG_SPIDER_SINGLE_STEP = new SingleStepPathingStrategy();
    private static final PathingStrategy BIG_SPIDER_A_STAR = new AStarPathingStrategy();

    public BigSpider(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, boolean canMove) {
        super(id, position, images, animationPeriod, actionPeriod, canMove);
    }
}
