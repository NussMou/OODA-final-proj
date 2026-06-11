import java.awt.Point;
import java.awt.Rectangle;

public class Link extends DiagramElement {
    private final ToolType linkType;
    private final BasicNode sourceNode;
    private final int sourcePortIndex;
    private final BasicNode targetNode;
    private final int targetPortIndex;

    public Link(ToolType linkType, BasicNode sourceNode, int sourcePortIndex,
            BasicNode targetNode, int targetPortIndex, int depth) {
        super(depth);
        this.linkType = linkType;
        this.sourceNode = sourceNode;
        this.sourcePortIndex = sourcePortIndex;
        this.targetNode = targetNode;
        this.targetPortIndex = targetPortIndex;
    }

    public BasicNode getSourceNode() {
        return sourceNode;
    }

    public BasicNode getTargetNode() {
        return targetNode;
    }

    public ToolType getLinkType() {
        return linkType;
    }

    public Point getSourcePoint() {
        return sourceNode.getPortCenterPoint(sourcePortIndex);
    }

    public Point getTargetPoint() {
        return targetNode.getPortCenterPoint(targetPortIndex);
    }

    @Override
    public boolean contains(Point point) {
        return GeometryUtil.distancePointToSegment(point, getSourcePoint(), getTargetPoint()) <= 6.0;
    }

    @Override
    public Rectangle getBounds() {
        Point sourcePoint = getSourcePoint();
        Point targetPoint = getTargetPoint();
        int x = Math.min(sourcePoint.x, targetPoint.x);
        int y = Math.min(sourcePoint.y, targetPoint.y);
        int width = Math.abs(sourcePoint.x - targetPoint.x);
        int height = Math.abs(sourcePoint.y - targetPoint.y);
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void moveBy(int deltaX, int deltaY) {
        // Links follow nodes dynamically, so no direct movement is required.
    }
}
