import java.awt.Point;
import java.awt.Rectangle;

public class RectNode extends BasicNode {
    public RectNode(int x, int y, int width, int height, int depth) {
        super(x, y, width, height, depth);
    }

    @Override
    public int getPortCount() {
        return 8;
    }

    @Override
    public Point getPortCenterPoint(int index) {
        int left = x;
        int right = x + width;
        int top = y;
        int bottom = y + height;
        int centerX = x + width / 2;
        int centerY = y + height / 2;

        switch (index) {
            case 0:
                return new Point(left, top);
            case 1:
                return new Point(centerX, top);
            case 2:
                return new Point(right, top);
            case 3:
                return new Point(left, centerY);
            case 4:
                return new Point(right, centerY);
            case 5:
                return new Point(left, bottom);
            case 6:
                return new Point(centerX, bottom);
            case 7:
                return new Point(right, bottom);
            default:
                return new Point(centerX, centerY);
        }
    }

    @Override
    public boolean contains(Point point) {
        return getBounds().contains(point);
    }

    @Override
    public void resizeFromPort(int portIndex, Point draggedPoint) {
        Rectangle newBounds = getBounds();

        switch (portIndex) {
            case 0:
                newBounds = GeometryUtil.buildBoundsFromTwoPoints(
                        new Point(x + width, y + height), draggedPoint);
                break;
            case 1:
                newBounds = GeometryUtil.buildBoundsFromFixedHorizontalEdge(
                        x, y + height, width, true, draggedPoint);
                break;
            case 2:
                newBounds = GeometryUtil.buildBoundsFromTwoPoints(
                        new Point(x, y + height), draggedPoint);
                break;
            case 3:
                newBounds = GeometryUtil.buildBoundsFromFixedVerticalEdge(
                        x + width, y, height, true, draggedPoint);
                break;
            case 4:
                newBounds = GeometryUtil.buildBoundsFromFixedVerticalEdge(
                        x, y, height, false, draggedPoint);
                break;
            case 5:
                newBounds = GeometryUtil.buildBoundsFromTwoPoints(
                        new Point(x + width, y), draggedPoint);
                break;
            case 6:
                newBounds = GeometryUtil.buildBoundsFromFixedHorizontalEdge(
                        x, y, width, false, draggedPoint);
                break;
            case 7:
                newBounds = GeometryUtil.buildBoundsFromTwoPoints(
                        new Point(x, y), draggedPoint);
                break;
            default:
                break;
        }

        setBounds(newBounds);
    }
}
