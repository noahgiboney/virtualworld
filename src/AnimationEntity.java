import processing.core.PImage;

import java.util.List;

public abstract class AnimationEntity extends Entity {

    private double animationPeriod;

    public AnimationEntity(String id, Point position, List<PImage> images, double animationPeriod) {
        super(id, position, images);
        this.animationPeriod = animationPeriod;
    }

    public double getAnimationPeriod() {
        return this.animationPeriod;
    }

    public void nextImage() {
        setImageIndex(getImageIndex() + 1);
    }
}
