import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

public class OvalNode extends BasicNode {
    public OvalNode(int x, int y, int width, int height, int depth) {
        super(x, y, width, height, depth);
    }

    @Override
    public int getPortCount() {
        return 4;
    }

    @Override
    public Point getPortCenterPoint(int index) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;

        switch (index) {
            case 0:
                return new Point(centerX, y);
            case 1:
                return new Point(x, centerY);
            case 2:
                return new Point(x + width, centerY);
            case 3:
                return new Point(centerX, y + height);
            default:
                return new Point(centerX, centerY);
        }
    }

    @Override
    public boolean contains(Point point) {
        Ellipse2D ellipse2d = new Ellipse2D.Double(x, y, width, height);
        return ellipse2d.contains(point);
    }

    @Override
    public void resizeFromPort(int portIndex, Point draggedPoint) {
        Rectangle newBounds = getBounds();

        switch (portIndex) {
            case 0:
                newBounds = GeometryUtil.buildBoundsFromFixedHorizontalEdge(
                        x, y + height, width, true, draggedPoint);
                break;
            case 1:
                newBounds = GeometryUtil.buildBoundsFromFixedVerticalEdge(
                        x + width, y, height, true, draggedPoint);
                break;
            case 2:
                newBounds = GeometryUtil.buildBoundsFromFixedVerticalEdge(
                        x, y, height, false, draggedPoint);
                break;
            case 3:
                newBounds = GeometryUtil.buildBoundsFromFixedHorizontalEdge(
                        x, y, width, false, draggedPoint);
                break;
            default:
                break;
        }

        setBounds(newBounds);
    }
}
