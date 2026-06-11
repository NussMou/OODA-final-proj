import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

public class DiagramRenderer {
    public void drawModel(Graphics2D graphics2d, EditorModel editorModel) {
        for (Link link : editorModel.getLinksInDrawOrder()) {
            drawLink(graphics2d, link);
        }
        for (Node node : editorModel.getTopLevelNodesInDrawOrder()) {
            drawNode(graphics2d, node);
        }
    }

    public void drawInteractionOverlays(Graphics2D graphics2d,
            ToolManager toolManager, EditorController editorController) {
        ToolType currentToolType = toolManager.getCurrentToolType();
        Point dragStartPoint = editorController.getDragStartPoint();
        Point dragCurrentPoint = editorController.getDragCurrentPoint();
        Port linkStartPort = editorController.getLinkStartPort();
        Rectangle selectionRectangle = editorController.getSelectionRectangle();

        if (currentToolType.isCreateTool() && dragStartPoint != null && dragCurrentPoint != null) {
            Rectangle bounds = GeometryUtil.buildBoundsFromTwoPoints(dragStartPoint, dragCurrentPoint);
            graphics2d.setColor(Color.GRAY);
            graphics2d.setStroke(new BasicStroke(1.5f));
            if (currentToolType == ToolType.RECT) {
                graphics2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            } else {
                graphics2d.drawOval(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }

        if (currentToolType.isLinkTool() && linkStartPort != null && dragCurrentPoint != null) {
            Point sourcePoint = linkStartPort.getCenterPoint();
            graphics2d.setColor(Color.GRAY);
            graphics2d.drawLine(sourcePoint.x, sourcePoint.y, dragCurrentPoint.x, dragCurrentPoint.y);
        }

        if (selectionRectangle != null) {
            graphics2d.setColor(new Color(40, 120, 220));
            float[] dash = {6.0f, 4.0f};
            graphics2d.setStroke(new BasicStroke(
                    1.5f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f,
                    dash,
                    0.0f));
            graphics2d.drawRect(
                    selectionRectangle.x,
                    selectionRectangle.y,
                    selectionRectangle.width,
                    selectionRectangle.height);
            graphics2d.setStroke(new BasicStroke(1.0f));
        }
    }

    public void drawNode(Graphics2D graphics2d, Node node) {
        if (node instanceof CompositeNode) {
            drawCompositeNode(graphics2d, (CompositeNode) node);
        } else if (node instanceof RectNode) {
            drawRectNode(graphics2d, (RectNode) node);
        } else if (node instanceof OvalNode) {
            drawOvalNode(graphics2d, (OvalNode) node);
        }
    }

    private void drawRectNode(Graphics2D graphics2d, RectNode node) {
        Rectangle bounds = node.getBounds();
        graphics2d.setColor(new Color(180, 180, 180));
        graphics2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        graphics2d.setColor(Color.BLACK);
        graphics2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        drawLabel(graphics2d, node);
        drawPortsIfNeeded(graphics2d, node);
    }

    private void drawOvalNode(Graphics2D graphics2d, OvalNode node) {
        Rectangle bounds = node.getBounds();
        graphics2d.setColor(new Color(180, 180, 180));
        graphics2d.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);
        graphics2d.setColor(Color.BLACK);
        graphics2d.drawOval(bounds.x, bounds.y, bounds.width, bounds.height);
        drawLabel(graphics2d, node);
        drawPortsIfNeeded(graphics2d, node);
    }

    private void drawCompositeNode(Graphics2D graphics2d, CompositeNode node) {
        for (Node child : node.getChildren()) {
            drawNode(graphics2d, child);
        }

        if (node.isSelected() || node.isHovered()) {
            drawDashedOutline(graphics2d, node.getBounds());
        }
    }

    private void drawLabel(Graphics2D graphics2d, BasicNode node) {
        String labelName = node.getLabelName();
        if (labelName == null || labelName.isEmpty()) {
            return;
        }

        Rectangle bounds = node.getBounds();
        FontMetrics fontMetrics = graphics2d.getFontMetrics();
        int padding = 6;
        int textWidth = fontMetrics.stringWidth(labelName);
        int textHeight = fontMetrics.getHeight();
        int boxWidth = textWidth + padding * 2;
        int boxHeight = textHeight + padding;
        int boxX = bounds.x + (bounds.width - boxWidth) / 2;
        int boxY = bounds.y + (bounds.height - boxHeight) / 2;

        graphics2d.setColor(node.getLabelColor());
        graphics2d.fillRect(boxX, boxY, boxWidth, boxHeight);
        graphics2d.setColor(Color.BLACK);
        graphics2d.drawRect(boxX, boxY, boxWidth, boxHeight);
        graphics2d.drawString(labelName, boxX + padding, boxY + fontMetrics.getAscent() + padding / 2);
    }

    private void drawPortsIfNeeded(Graphics2D graphics2d, BasicNode node) {
        if (node.isSelected() || node.isHovered()) {
            for (Port port : node.getPorts()) {
                drawPort(graphics2d, port);
            }
        }
    }

    private void drawPort(Graphics2D graphics2d, Port port) {
        Rectangle bounds = port.getBounds();
        graphics2d.setColor(Color.BLACK);
        graphics2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void drawLink(Graphics2D graphics2d, Link link) {
        Point sourcePoint = link.getSourcePoint();
        Point targetPoint = link.getTargetPoint();

        graphics2d.setColor(Color.BLACK);
        graphics2d.setStroke(new BasicStroke(2.0f));
        graphics2d.drawLine(sourcePoint.x, sourcePoint.y, targetPoint.x, targetPoint.y);

        if (link.getLinkType() == ToolType.ASSOCIATION) {
            drawOpenArrow(graphics2d, sourcePoint, targetPoint);
        } else if (link.getLinkType() == ToolType.GENERALIZATION) {
            drawTriangle(graphics2d, sourcePoint, targetPoint);
        } else if (link.getLinkType() == ToolType.COMPOSITION) {
            drawDiamond(graphics2d, sourcePoint, targetPoint);
        }
    }

    private void drawOpenArrow(Graphics2D graphics2d, Point from, Point to) {
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        int length = 14;
        int x1 = (int) (to.x - length * Math.cos(angle - Math.PI / 6));
        int y1 = (int) (to.y - length * Math.sin(angle - Math.PI / 6));
        int x2 = (int) (to.x - length * Math.cos(angle + Math.PI / 6));
        int y2 = (int) (to.y - length * Math.sin(angle + Math.PI / 6));
        graphics2d.drawLine(to.x, to.y, x1, y1);
        graphics2d.drawLine(to.x, to.y, x2, y2);
    }

    private void drawTriangle(Graphics2D graphics2d, Point from, Point to) {
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        int length = 18;
        int halfWidth = 8;
        int backX = (int) (to.x - length * Math.cos(angle));
        int backY = (int) (to.y - length * Math.sin(angle));
        int x1 = (int) (backX + halfWidth * Math.sin(angle));
        int y1 = (int) (backY - halfWidth * Math.cos(angle));
        int x2 = (int) (backX - halfWidth * Math.sin(angle));
        int y2 = (int) (backY + halfWidth * Math.cos(angle));

        Polygon polygon = new Polygon();
        polygon.addPoint(to.x, to.y);
        polygon.addPoint(x1, y1);
        polygon.addPoint(x2, y2);

        graphics2d.setColor(Color.WHITE);
        graphics2d.fillPolygon(polygon);
        graphics2d.setColor(Color.BLACK);
        graphics2d.drawPolygon(polygon);
    }

    private void drawDiamond(Graphics2D graphics2d, Point from, Point to) {
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        int length = 14;
        int halfWidth = 8;

        int p1x = to.x;
        int p1y = to.y;
        int p2x = (int) (to.x - length * Math.cos(angle) + halfWidth * Math.sin(angle));
        int p2y = (int) (to.y - length * Math.sin(angle) - halfWidth * Math.cos(angle));
        int p3x = (int) (to.x - 2 * length * Math.cos(angle));
        int p3y = (int) (to.y - 2 * length * Math.sin(angle));
        int p4x = (int) (to.x - length * Math.cos(angle) - halfWidth * Math.sin(angle));
        int p4y = (int) (to.y - length * Math.sin(angle) + halfWidth * Math.cos(angle));

        Polygon polygon = new Polygon();
        polygon.addPoint(p1x, p1y);
        polygon.addPoint(p2x, p2y);
        polygon.addPoint(p3x, p3y);
        polygon.addPoint(p4x, p4y);

        graphics2d.setColor(Color.BLACK);
        graphics2d.fillPolygon(polygon);
    }

    public void drawDashedOutline(Graphics2D graphics2d, Rectangle bounds) {
        graphics2d.setColor(new Color(30, 90, 180));
        float[] dash = {8.0f, 4.0f};
        graphics2d.setStroke(new BasicStroke(
                2.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f,
                dash,
                0.0f));
        graphics2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        graphics2d.setStroke(new BasicStroke(1.0f));
    }
}
