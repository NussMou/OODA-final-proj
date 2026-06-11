import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeNode extends Node {
    private final List<Node> children;

    public CompositeNode(List<Node> children, int depth) {
        super(0, 0, 0, 0, depth);
        this.children = new ArrayList<>(children);
        recomputeBounds();
    }

    public List<Node> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void recomputeBounds() {
        if (children.isEmpty()) {
            setBounds(new Rectangle(0, 0, GeometryUtil.MIN_SIZE, GeometryUtil.MIN_SIZE));
            return;
        }

        Rectangle unionBounds = new Rectangle(children.get(0).getBounds());
        for (int i = 1; i < children.size(); ++i) {
            unionBounds = unionBounds.union(children.get(i).getBounds());
        }
        Rectangle squareBounds = GeometryUtil.makeSquareBounds(unionBounds);
        setBounds(squareBounds);
    }

    @Override
    public void moveBy(int deltaX, int deltaY) {
        for (Node child : children) {
            child.moveBy(deltaX, deltaY);
        }
        recomputeBounds();
    }

    @Override
    public boolean contains(Point point) {
        return getBounds().contains(point);
    }

    @Override
    public boolean isBasicNode() {
        return false;
    }

    @Override
    public boolean isCompositeNode() {
        return true;
    }

    @Override
    public BasicNode findBasicNodeContaining(Point point) {
        for (int i = children.size() - 1; i >= 0; --i) {
            BasicNode basicNode = children.get(i).findBasicNodeContaining(point);
            if (basicNode != null) {
                return basicNode;
            }
        }
        return null;
    }
}
