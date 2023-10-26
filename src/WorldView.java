import processing.core.PApplet;
import processing.core.PImage;

import java.util.Optional;

public final class WorldView {
    private PApplet screen;
    private WorldModel world;
    private int tileWidth;
    private int tileHeight;
    private Viewport viewport;

    public WorldView(int numRows, int numCols, PApplet screen, WorldModel world, int tileWidth, int tileHeight) {
        this.screen = screen;
        this.world = world;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.viewport = new Viewport(numRows, numCols);
    }
    public Viewport viewport(){return this.viewport;}
    public int clamp(int value, int low, int high) {
        return Math.min(high, Math.max(value, low));
    }

    public void shiftView( int colDelta, int rowDelta) {
        int newCol = clamp(viewport.col() + colDelta, 0, world.numCols() - viewport.numCols());
        int newRow = clamp(viewport.row() + rowDelta, 0, world.numRows() - viewport.numRows());

        viewport.shift(newCol, newRow);
    }

    public void drawEntities() {
        for (Entity entity : this.world.entities()) {
            Point pos = entity.getPosition();

            if (this.viewport.contains( pos)) {
                Point viewPoint = this.viewport.worldToViewport(pos.getX(), pos.getY());
                this.screen.image(entity.getCurrentImage(), viewPoint.getX() * this.tileWidth,
                        viewPoint.getY() * this.tileHeight);
            }
        }
    }

    public void drawViewport() {
        this.drawBackground();
        this.drawEntities();
    }

    public void drawBackground() {
        for (int row = 0; row < this.viewport.numRows(); row++) {
            for (int col = 0; col < this.viewport.numCols(); col++) {
                Point worldPoint = this.viewport.viewportToWorld(col, row);
                Optional<PImage> image = this.world.getBackgroundImage( worldPoint);
                if (image.isPresent()) {
                    this.screen.image(image.get(), col * this.tileWidth, row * this.tileHeight);
                }
            }
        }
    }
}
