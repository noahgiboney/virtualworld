import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class  WorldModel {
    public static final String FAIRY_KEY = "fairy";
    public static final int FAIRY_ANIMATION_PERIOD = 0;
    public static final int FAIRY_ACTION_PERIOD = 1;

    public static final int PROPERTY_KEY = 0;
    public static final int PROPERTY_ID = 1;
    public static final int PROPERTY_COL = 2;
    public static final int PROPERTY_ROW = 3;
    public static final int ENTITY_NUM_PROPERTIES = 4;

    private int numRows;
    private int numCols;
    private Background[][] background;
    private Entity[][] occupancy;
    private Set<Entity> entities;

    public WorldModel() {}
    public Set<Entity> entities(){return this.entities;}
    public int numRows(){return this.numRows;}
    public int numCols(){return this.numCols;}
    public Entity getOccupancyCell( Point pos) {
        return this.occupancy[pos.getY()][pos.getX()];
    }

    public void setOccupancyCell(Point pos, Entity entity) {

        this.occupancy[pos.getY()][pos.getX()] = entity;
    }

    public void removeEntityAt(Point pos) {
        if (this.withinBounds(pos) && this.getOccupancyCell(pos) != null) {
            Entity entity = this.getOccupancyCell( pos);

            /* This moves the entity just outside the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            this.setOccupancyCell(pos, null);
        }
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (this.isOccupied(pos)) {
            return Optional.of(this.getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }

    public void moveEntity(EventScheduler scheduler, Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (this.withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            Optional<Entity> occupant = this.getOccupant(pos);
            occupant.ifPresent(target -> this.removeEntity(scheduler, target));
            this.setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }
    /*
           Assumes that there is no entity currently occupying the
           intended destination cell.
        */
    public void addEntity(Entity entity) {
        if (this.withinBounds(entity.getPosition())) {
            this.setOccupancyCell(entity.getPosition(), entity);
            this.entities().add(entity);
        }
    }
    public void removeEntity( EventScheduler scheduler, Entity entity) {
        scheduler.unscheduleAllEvents(entity);
        this.removeEntityAt(entity.getPosition());
    }
    public Optional<Entity> findNearest(Point pos, String targetKey) {
        List<Entity> ofType = new LinkedList<>();
            for (Entity entity : this.entities) {
                if (entity.getKey().equals(targetKey)) {
                    ofType.add(entity);
                }
            }
        return pos.nearestEntity(ofType, pos);
    }


    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }

    public boolean withinBounds(Point pos) {
        return pos.getY() >= 0 && pos.getY() < this.numRows &&
                pos.getX() >= 0 && pos.getX() < this.numCols;
    }


    public Background getBackgroundCell(Point pos) {
        return this.background[pos.getY()][pos.getX()];
    }

    public void setBackgroundCell(Point pos, Background background) {
        this.background[pos.getY()][pos.getX()] = background;
    }

    public Optional<PImage> getBackgroundImage( Point pos) {
        if (withinBounds(pos)) {
            return Optional.of((getBackgroundCell(pos).getCurrentImage()));
        } else {
            return Optional.empty();
        }
    }

    public void load(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        parseSaveFile(saveFile, imageStore, defaultBackground);
        if(this.background == null){
            this.background = new Background[this.numRows][this.numCols];
            for (Background[] row : this.background)
                Arrays.fill(row, defaultBackground);
        }
        if(this.occupancy == null){
            this.occupancy = new Entity[this.numRows][this.numCols];
            this.entities = new HashSet<>();
        }
    }

    public void parseSaveFile(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        String lastHeader = "";
        int headerLine = 0;
        int lineCounter = 0;
        while(saveFile.hasNextLine()){
            lineCounter++;
            String line = saveFile.nextLine().strip();
            if(line.endsWith(":")){
                headerLine = lineCounter;
                lastHeader = line;
                switch (line){
                    case "Backgrounds:" -> this.background = new Background[this.numRows][this.numCols];
                    case "Entities:" -> {
                        this.occupancy = new Entity[this.numRows][this.numCols];
                        this.entities = new HashSet<>();
                    }
                }
            }else{
                switch (lastHeader){
                    case "Rows:" -> this.numRows = Integer.parseInt(line);
                    case "Cols:" -> this.numCols = Integer.parseInt(line);
                    case "Backgrounds:" -> this.parseBackgroundRow(line, lineCounter-headerLine-1, imageStore);
                    case "Entities:" -> parseEntity(this, line, imageStore);
                }
            }
        }
    }

    public static void parseEntity(WorldModel world, String line, ImageStore imageStore) {
        String[] properties = line.split(" ", ENTITY_NUM_PROPERTIES + 1);
        if (properties.length >= ENTITY_NUM_PROPERTIES) {
            String key = properties[PROPERTY_KEY];
            String id = properties[PROPERTY_ID];
            Point pt = new Point(Integer.parseInt(properties[PROPERTY_COL]), Integer.parseInt(properties[PROPERTY_ROW]));

            properties = properties.length == ENTITY_NUM_PROPERTIES ?
                    new String[0] : properties[ENTITY_NUM_PROPERTIES].split(" ");

            switch (key) {
                case Obstacle.OBSTACLE_KEY -> {
                    Obstacle entity = new Obstacle(id, pt, imageStore.getImageList(Obstacle.OBSTACLE_KEY),
                            Double.parseDouble(properties[Obstacle.OBSTACLE_ANIMATION_PERIOD]));
                    world.tryAddEntity(entity);
                }
                case Dude.DUDE_KEY -> {
                    DudeNotFull entity = new DudeNotFull(id, pt, imageStore.getImageList(Dude.DUDE_KEY), Double.parseDouble(properties[Dude.DUDE_ANIMATION_PERIOD]),
                            Double.parseDouble(properties[Dude.DUDE_ACTION_PERIOD]), Integer.parseInt(properties[Dude.DUDE_LIMIT]));
                    world.tryAddEntity(entity);
                }
                case FAIRY_KEY -> {
                    Fairy entity = new Fairy(id, pt, imageStore.getImageList(FAIRY_KEY) , Double.parseDouble(properties[FAIRY_ANIMATION_PERIOD]),
                            Double.parseDouble(properties[FAIRY_ACTION_PERIOD]));
                    world.tryAddEntity(entity);
                }
                case House.HOUSE_KEY -> {
                    House entity = new House(id, pt, imageStore.getImageList(House.HOUSE_KEY));
                    world.tryAddEntity(entity);
                }
                case Tree.TREE_KEY -> {
                    Tree entity = new Tree(id, pt, imageStore.getImageList(Tree.TREE_KEY),Double.parseDouble(properties[Tree.TREE_ANIMATION_PERIOD]),
                            Double.parseDouble(properties[Tree.TREE_ACTION_PERIOD]), Integer.parseInt(properties[Tree.TREE_HEALTH]));
                    world.tryAddEntity(entity);
                }
                case Sapling.SAPLING_KEY -> {
                    int health = Integer.parseInt(properties[Sapling.SAPLING_HEALTH]);
                    Sapling entity = new Sapling(id, pt, imageStore.getImageList(Sapling.SAPLING_KEY), Sapling.SAPLING_ACTION_ANIMATION_PERIOD, Sapling.SAPLING_ACTION_ANIMATION_PERIOD, health, Sapling.SAPLING_HEALTH_LIMIT);
                    world.tryAddEntity(entity);
                }
                case Stump.STUMP_KEY -> {
                        Stump entity = new Stump(id, pt, imageStore.getImageList(Stump.STUMP_KEY));
                        world.tryAddEntity(entity);
                }
                default -> throw new IllegalArgumentException("Entity key is unknown");
            }
        }else{
            throw new IllegalArgumentException("Entity must be formatted as [key] [id] [x] [y] ...");
        }
    }

    public void tryAddEntity(Entity entity) {
        if (isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity(entity);
    }

    public void parseBackgroundRow(String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if(row < this.numRows){
            int rows = Math.min(cells.length, this.numCols);
            for (int col = 0; col < rows; col++){
                this.background[row][col] = new Background(
                        imageStore.getImageList(cells[col]));
            }
        }
    }

    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Entity entity : entities) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }
}
