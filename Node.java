import java.awt.Point;
import java.awt.Rectangle;

public abstract class Node extends DiagramElement {
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected Node(int x, int y, int width, int height, int depth) {
        super(depth);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getXValue() {
        return x;
    }

    public int getYValue() {
        return y;
    }

    public int getWidthValue() {
        return width;
    }

    public int getHeightValue() {
        return height;
    }

    public void setBounds(Rectangle bounds) {
        x = bounds.x;
        y = bounds.y;
        width = bounds.width;
        height = bounds.height;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void moveBy(int deltaX, int deltaY) {
        x += deltaX;
        y += deltaY;
    }

    public abstract boolean isBasicNode();

    public abstract boolean isCompositeNode();

    public BasicNode findBasicNodeContaining(Point point) {
        return null;
    }
}
