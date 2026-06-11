import java.awt.Point;
import java.awt.Rectangle;

public final class GeometryUtil {
    public static final int MIN_SIZE = 20;

    private GeometryUtil() {
    }

    public static Rectangle buildBoundsFromTwoPoints(Point fixedPoint, Point movingPoint) {
        int left = Math.min(fixedPoint.x, movingPoint.x);
        int right = Math.max(fixedPoint.x, movingPoint.x);
        int top = Math.min(fixedPoint.y, movingPoint.y);
        int bottom = Math.max(fixedPoint.y, movingPoint.y);

        if (right - left < MIN_SIZE) {
            if (movingPoint.x >= fixedPoint.x) {
                right = fixedPoint.x + MIN_SIZE;
                left = fixedPoint.x;
            } else {
                left = fixedPoint.x - MIN_SIZE;
                right = fixedPoint.x;
            }
        }

        if (bottom - top < MIN_SIZE) {
            if (movingPoint.y >= fixedPoint.y) {
                bottom = fixedPoint.y + MIN_SIZE;
                top = fixedPoint.y;
            } else {
                top = fixedPoint.y - MIN_SIZE;
                bottom = fixedPoint.y;
            }
        }

        return new Rectangle(left, top, right - left, bottom - top);
    }

    public static Rectangle normalizeRectangle(Point startPoint, Point endPoint) {
        int x = Math.min(startPoint.x, endPoint.x);
        int y = Math.min(startPoint.y, endPoint.y);
        int width = Math.abs(startPoint.x - endPoint.x);
        int height = Math.abs(startPoint.y - endPoint.y);
        return new Rectangle(x, y, width, height);
    }

    public static Rectangle buildBoundsFromFixedVerticalEdge(
            int fixedX, int originalY, int originalHeight,
            boolean dragLeftEdge, Point movingPoint) {
        int left = Math.min(fixedX, movingPoint.x);
        int right = Math.max(fixedX, movingPoint.x);

        if (right - left < MIN_SIZE) {
            if (movingPoint.x >= fixedX) {
                right = fixedX + MIN_SIZE;
                left = fixedX;
            } else {
                left = fixedX - MIN_SIZE;
                right = fixedX;
            }
        }

        return new Rectangle(left, originalY, right - left, originalHeight);
    }

    public static Rectangle buildBoundsFromFixedHorizontalEdge(
            int originalX, int fixedY, int originalWidth,
            boolean dragTopEdge, Point movingPoint) {
        int top = Math.min(fixedY, movingPoint.y);
        int bottom = Math.max(fixedY, movingPoint.y);

        if (bottom - top < MIN_SIZE) {
            if (movingPoint.y >= fixedY) {
                bottom = fixedY + MIN_SIZE;
                top = fixedY;
            } else {
                top = fixedY - MIN_SIZE;
                bottom = fixedY;
            }
        }

        return new Rectangle(originalX, top, originalWidth, bottom - top);
    }

    public static Rectangle makeSquareBounds(Rectangle rectangle) {
        int size = Math.max(rectangle.width, rectangle.height);
        return new Rectangle(rectangle.x, rectangle.y, size, size);
    }

    public static double distancePointToSegment(Point point, Point startPoint, Point endPoint) {
        double dx = endPoint.x - startPoint.x;
        double dy = endPoint.y - startPoint.y;

        if (dx == 0.0 && dy == 0.0) {
            dx = point.x - startPoint.x;
            dy = point.y - startPoint.y;
            return Math.sqrt(dx * dx + dy * dy);
        }

        double t = ((point.x - startPoint.x) * dx + (point.y - startPoint.y) * dy) / (dx * dx + dy * dy);
        t = Math.max(0.0, Math.min(1.0, t));

        double projectionX = startPoint.x + t * dx;
        double projectionY = startPoint.y + t * dy;
        double deltaX = point.x - projectionX;
        double deltaY = point.y - projectionY;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
}