import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

public class EditorController {
    private final EditorModel editorModel;
    private final ToolManager toolManager;
    private final Runnable stateChangedCallback;
    private final JComponent canvas;

    private Point dragStartPoint;
    private Point dragCurrentPoint;
    private Node draggingNode;
    private Node pendingSingleSelectionNode;
    private List<Node> draggingNodes;
    private boolean draggedDuringCurrentPress;
    private Port resizePort;
    private Port linkStartPort;
    private Rectangle selectionRectangle;

    public EditorController(EditorModel editorModel, ToolManager toolManager,
            Runnable stateChangedCallback, JComponent canvas) {
        this.editorModel = editorModel;
        this.toolManager = toolManager;
        this.stateChangedCallback = stateChangedCallback;
        this.canvas = canvas;
    }

    public void handleMousePressed(MouseEvent event) {
        Point point = event.getPoint();
        ToolType currentToolType = toolManager.getCurrentToolType();

        dragStartPoint = point;
        dragCurrentPoint = point;
        draggingNode = null;
        pendingSingleSelectionNode = null;
        draggingNodes = null;
        draggedDuringCurrentPress = false;
        resizePort = null;
        linkStartPort = null;
        selectionRectangle = null;

        if (currentToolType.isCreateTool()) {
            return;
        }

        if (currentToolType.isLinkTool()) {
            linkStartPort = editorModel.findPortAt(point);
            canvas.repaint();
            return;
        }

        if (currentToolType == ToolType.SELECT) {
            Port pressedPort = editorModel.findPortAt(point);
            Node topNode = editorModel.findTopMostNode(point);

            if (pressedPort != null) {
                editorModel.setSingleSelection(pressedPort.getOwnerNode());
                resizePort = pressedPort;
                syncToolbarState();
                canvas.repaint();
                return;
            }

            if (topNode != null) {
                if (editorModel.getSelectedNodes().contains(topNode)) {
                    pendingSingleSelectionNode = topNode;
                    draggingNodes = editorModel.getSelectedNodeList();
                } else {
                    editorModel.setSingleSelection(topNode);
                    draggingNodes = new ArrayList<>();
                    draggingNodes.add(topNode);
                }
                draggingNode = topNode;
                syncToolbarState();
                canvas.repaint();
                return;
            }

            editorModel.clearSelection();
            selectionRectangle = new Rectangle(point.x, point.y, 0, 0);
            canvas.repaint();
        }
    }

    public void handleMouseDragged(MouseEvent event) {
        Point point = event.getPoint();
        ToolType currentToolType = toolManager.getCurrentToolType();
        dragCurrentPoint = point;

        if (currentToolType.isCreateTool()) {
            canvas.repaint();
            return;
        }

        if (currentToolType.isLinkTool()) {
            editorModel.updateHover(point);
            canvas.repaint();
            return;
        }

        if (resizePort != null) {
            draggedDuringCurrentPress = true;
            resizePort.getOwnerNode().resizeFromPort(resizePort.getIndex(), point);
            canvas.repaint();
            return;
        }

        if (draggingNode != null) {
            int deltaX = point.x - dragStartPoint.x;
            int deltaY = point.y - dragStartPoint.y;
            if (deltaX != 0 || deltaY != 0) {
                draggedDuringCurrentPress = true;
            }
            for (Node node : draggingNodes) {
                node.moveBy(deltaX, deltaY);
            }
            dragStartPoint = point;
            canvas.repaint();
            return;
        }

        if (selectionRectangle != null) {
            selectionRectangle = GeometryUtil.normalizeRectangle(dragStartPoint, point);
            canvas.repaint();
        }
    }

    public void handleMouseReleased(MouseEvent event) {
        Point point = event.getPoint();
        ToolType currentToolType = toolManager.getCurrentToolType();
        dragCurrentPoint = point;

        if (currentToolType.isCreateTool()) {
            createNodeFromDrag();
            toolManager.finishTemporaryCreateTool();
            clearInteractionState();
            syncToolbarState();
            canvas.repaint();
            return;
        }

        if (currentToolType.isLinkTool()) {
            finalizeLink(point);
            clearInteractionState();
            canvas.repaint();
            return;
        }

        if (resizePort != null || draggingNode != null) {
            if (draggingNode != null && !draggedDuringCurrentPress && pendingSingleSelectionNode != null) {
                editorModel.setSingleSelection(pendingSingleSelectionNode);
            }
            clearInteractionState();
            canvas.repaint();
            return;
        }

        if (selectionRectangle != null) {
            List<Node> selectedNodes = editorModel.findNodesFullyInside(selectionRectangle);
            editorModel.setMultiSelection(selectedNodes);
            clearInteractionState();
            canvas.repaint();
        }
    }

    public void handleMouseMoved(MouseEvent event) {
        editorModel.updateHover(event.getPoint());
        canvas.repaint();
    }

    public void chooseTool(ToolType toolType) {
        if (toolType.isCreateTool()) {
            toolManager.beginTemporaryCreateTool(toolType);
        } else {
            toolManager.setPersistentTool(toolType);
        }
        syncToolbarState();
        canvas.repaint();
    }

    public Point getDragStartPoint() {
        return dragStartPoint;
    }

    public Point getDragCurrentPoint() {
        return dragCurrentPoint;
    }

    public Port getLinkStartPort() {
        return linkStartPort;
    }

    public Rectangle getSelectionRectangle() {
        return selectionRectangle;
    }

    private void createNodeFromDrag() {
        if (dragStartPoint == null || dragCurrentPoint == null) {
            return;
        }

        Rectangle bounds = GeometryUtil.buildBoundsFromTwoPoints(dragStartPoint, dragCurrentPoint);

        if (toolManager.getCurrentToolType() == ToolType.RECT) {
            editorModel.addNode(new RectNode(bounds.x, bounds.y, bounds.width, bounds.height, 0));
        } else if (toolManager.getCurrentToolType() == ToolType.OVAL) {
            editorModel.addNode(new OvalNode(bounds.x, bounds.y, bounds.width, bounds.height, 0));
        }
    }

    private void finalizeLink(Point point) {
        if (linkStartPort == null) {
            return;
        }

        Port targetPort = editorModel.findPortAt(point);
        if (targetPort == null) {
            return;
        }
        if (targetPort.getOwnerNode() == linkStartPort.getOwnerNode()) {
            return;
        }

        Link link = new Link(
                toolManager.getCurrentToolType(),
                linkStartPort.getOwnerNode(),
                linkStartPort.getIndex(),
                targetPort.getOwnerNode(),
                targetPort.getIndex(),
                0);
        editorModel.addLink(link);
    }

    private void clearInteractionState() {
        dragStartPoint = null;
        dragCurrentPoint = null;
        draggingNode = null;
        pendingSingleSelectionNode = null;
        draggingNodes = null;
        draggedDuringCurrentPress = false;
        resizePort = null;
        linkStartPort = null;
        selectionRectangle = null;
    }

    private void syncToolbarState() {
        if (stateChangedCallback != null) {
            stateChangedCallback.run();
        }
    }
}
