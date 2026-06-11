import java.awt.Point;
import java.awt.Rectangle;

public abstract class DiagramElement {
    private int depth;
    private boolean selected;
    private boolean hovered;

    protected DiagramElement(int depth) {
        this.depth = depth;
        this.selected = false;
        this.hovered = false;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public abstract boolean contains(Point point);

    public abstract Rectangle getBounds();

    public abstract void moveBy(int deltaX, int deltaY);
}
