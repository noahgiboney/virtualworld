import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private int clickCount = 0;

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



    public void update(double frameTime){
        scheduler.updateOnTime(frameTime);
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    public void mousePressed() {
        clickCount++;
        System.out.println(clickCount);

        Point pressed = mouseToPoint();
        System.out.println("CLICK! " + pressed.getX() + ", " + pressed.getY());


        Optional<Entity> volacno = world.findNearest(pressed, Volcano.class);

        if(volacno.isPresent()){
            if (volacno.get() instanceof Volcano temp && clickCount == 1){
                temp.setErupted(true);
                temp.ScheduleActions(scheduler, world, imageStore);

                List<Point> lavaPoints = world.removeAllEntitiesOfType(Obstacle.class, scheduler);

                for(Point index : lavaPoints){
                    Obstacle lava = new Obstacle(Obstacle.LAVA_KEY, index, imageStore.getImageList(Obstacle.LAVA_KEY), random(0.3f,0.8f));
                    world.addEntity(lava);
                    lava.ScheduleActions(scheduler, world, imageStore);
                }

                ScheduledExecutorService repeater = Executors.newScheduledThreadPool(1);

                Runnable task = () -> {
                    Spider spider = new Spider(Spider.SPIDER_KEY, new Point(8, 13), imageStore.getImageList(Spider.SPIDER_KEY), 0.4,
                            random(0.3f,0.7f), true);
                    world.tryAddEntity(spider);
                    spider.ScheduleActions(scheduler, world, imageStore);
                };

                repeater.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
            }
        }
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
