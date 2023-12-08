import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import processing.core.*;

public final class VirtualWorld extends PApplet {
    private static String[] ARGS;
    private final int VIEW_WIDTH = 640;
    private final int VIEW_HEIGHT = 480;
    private final int TILE_WIDTH = 32;
    private final int TILE_HEIGHT = 32;
    private int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    private final String DEFAULT_IMAGE_NAME = "background_default";
    private final int DEFAULT_IMAGE_COLOR = 0x808080;
    private final String FAST_FLAG = "-fast";
    private final String FASTER_FLAG = "-faster";
    private final String FASTEST_FLAG = "-fastest";
    private final double FAST_SCALE = 0.5;
    private final double FASTER_SCALE = 0.25;
    private final double FASTEST_SCALE = 0.10;
    private final int KEYED_IMAGE_MIN = 5;
    private final int COLOR_MASK = 0xffffff;

    private double timeScale = 1.0;
    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;
    private String loadFile = "world.sav";
    private long startTimeMillis = 0;

    /*
      Called with color for which alpha should be set and alpha value.
      setAlpha(img, color(255, 255, 255), 0));
    */
    public void setAlpha(PImage img, int maskColor, int alpha) {
        int alphaValue = alpha << 24;
        int nonAlpha = maskColor & COLOR_MASK;
        img.format = ARGB;
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            if ((img.pixels[i] & COLOR_MASK) == nonAlpha) {
                img.pixels[i] = alphaValue | nonAlpha;
            }
        }
        img.updatePixels();
    }

    public void processImageLine(Map<String, List<PImage>> images, String line, PApplet screen) {
        String[] attrs = line.split("\\s");
        if (attrs.length >= 2) {
            String key = attrs[0];
            PImage img = screen.loadImage(attrs[1]);
            if (img != null && img.width != -1) {
                List<PImage> imgs = getImages(images, key);
                imgs.add(img);

                if (attrs.length >= KEYED_IMAGE_MIN) {
                    int KEYED_RED_IDX = 2;
                    int r = Integer.parseInt(attrs[KEYED_RED_IDX]);
                    int KEYED_GREEN_IDX = 3;
                    int g = Integer.parseInt(attrs[KEYED_GREEN_IDX]);
                    int KEYED_BLUE_IDX = 4;
                    int b = Integer.parseInt(attrs[KEYED_BLUE_IDX]);
                    setAlpha(img, screen.color(r, g, b), 0);
                }
            }
        }
    }

    public List<PImage> getImages(Map<String, List<PImage>> images, String key) {
        return images.computeIfAbsent(key, k -> new LinkedList<>());
    }

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        parseCommandLine(ARGS);
        String IMAGE_LIST_FILE_NAME = "imagelist";
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);

        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        this.scheduleActions(world, scheduler, imageStore);
    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = (appTime - scheduler.currentTime()) / timeScale;

        this.update(frameTime);
        view.drawViewport();
    }


    public void update(double frameTime) {
        scheduler.updateOnTime(frameTime);
    }

    public void mousePressed() {
        Point pressed = mouseToPoint();
        System.out.println(pressed.getX() + ", " + pressed.getY());

        //find the volcano that was clicked, erupt if it was within 5 tiles
        Optional<Entity> nearestVolcano = world.findNearest(pressed, Volcano.class);
        if (nearestVolcano.isPresent() && pressed.distanceSquared(nearestVolcano.get().getPosition()) <= 5) {
            if (nearestVolcano.get() instanceof Volcano volcano && !volcano.isErupted()) {

                volcano.setErupted(true); //erupt the volcano
                volcano.ScheduleActions(scheduler, world, imageStore); //eruption animation

                //build a list of obstacles (water & lava)
                List<Entity> obstacleList = world.findAllEntities(Obstacle.class, scheduler);

                //filter the list to only have water points and sort then based closest to the volcano
                List<Entity> filteredList = obstacleList.stream()
                        .filter(obstacle -> !Objects.equals(obstacle.getId(), Obstacle.LAVA_KEY))
                        .sorted(Comparator.comparingInt(o -> o.getPosition().calcDistanceFromStart(volcano.getPosition())))
                        .toList();

                //replace the nearest 4 water tiles with lava
                if (filteredList.size() >= 4) {
                    Entity lava1 = filteredList.get(0);
                    Entity lava2 = filteredList.get(1);
                    Entity lava3 = filteredList.get(2);
                    Entity lava4 = filteredList.get(3);
                    placeLava(lava1, world, scheduler, imageStore);
                    placeLava(lava2, world, scheduler, imageStore);
                    placeLava(lava3, world, scheduler, imageStore);
                    placeLava(lava4, world, scheduler, imageStore);
                } else if (!filteredList.isEmpty()) {
                    Entity closestEntity = filteredList.get(0);
                    placeLava(closestEntity, world, scheduler, imageStore);
                }
            }

            //spawn 6 spiders in the nests across the map when the volcano erupts
            Point[] spiderPoints = {new Point(13, 1), new Point(0, 15), new Point(16, 14), new Point(3, 25), new Point(29, 7), new Point(22, 20)};
            for (Point index : spiderPoints) {
                if (!world.isOccupied(index)) {
                    Spider spider = new Spider(Spider.SPIDER_KEY, index, imageStore.getImageList(Spider.SPIDER_KEY), Spider.SPIDER_ANIMATION_PERIOD,
                            Spider.SPIDER_ACTION_PERIOD);
                    world.tryAddEntity(spider);
                    spider.ScheduleActions(scheduler, world, imageStore);
                }
            }
        }
    }

    //helper method for placing lava
    private void placeLava(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Point currentSpot = entity.getPosition();

        //remove lava
        world.removeEntityAt(currentSpot);

        //add lava, gives it random animation period for visual effect
        Obstacle lava = new Obstacle(Obstacle.LAVA_KEY, currentSpot, imageStore.getImageList(Obstacle.LAVA_KEY), random(0.3f, 0.8f));
        world.tryAddEntity(lava);
        lava.ScheduleActions(scheduler, world, imageStore);
    }


    private Point mouseToPoint() {
        return view.viewport().viewportToWorld(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
    }

    public void keyPressed() {

        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }
            view.shiftView(dx, dy);
        }
    }

    public Background createDefaultBackground(ImageStore imageStore) {
        return new Background(imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    public void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            world.load(in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            world.load(in, imageStore, createDefaultBackground(imageStore));
        }
    }

    public void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            imageStore.loadImages(in, this, this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        for (Entity entity : world.entities()) {
            if(entity instanceof AnimationEntity){
                ((AnimationEntity) entity).ScheduleActions(scheduler, world, imageStore);
            }
        }
    }

    public void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                default -> loadFile = arg;
            }
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }
}
