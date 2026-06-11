import java.awt.Point;
import java.awt.Rectangle;

public class Port {
    public static final int DRAW_SIZE = 10;
    public static final int HIT_SIZE = 20;

    private final BasicNode ownerNode;
    private final int index;

    public Port(BasicNode ownerNode, int index) {
        this.ownerNode = ownerNode;
        this.index = index;
    }

    public BasicNode getOwnerNode() {
        return ownerNode;
    }

    public int getIndex() {
        return index;
    }

    public Point getCenterPoint() {
        return ownerNode.getPortCenterPoint(index);
    }

    public Rectangle getBounds() {
        Point centerPoint = getCenterPoint();
        return new Rectangle(
                centerPoint.x - DRAW_SIZE / 2,
                centerPoint.y - DRAW_SIZE / 2,
                DRAW_SIZE,
                DRAW_SIZE);
    }

    public Rectangle getHitBounds() {
        Point centerPoint = getCenterPoint();
        return new Rectangle(
                centerPoint.x - HIT_SIZE / 2,
                centerPoint.y - HIT_SIZE / 2,
                HIT_SIZE,
                HIT_SIZE);
    }

    public boolean contains(Point point) {
        return getHitBounds().contains(point);
    }

}
