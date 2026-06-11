import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class EditorCanvas extends JPanel {
    private final EditorModel editorModel;
    private final ToolManager toolManager;
    private final DiagramRenderer diagramRenderer;
    private final EditorController editorController;

    public EditorCanvas(EditorModel editorModel, ToolManager toolManager, Runnable stateChangedCallback) {
        this.editorModel = editorModel;
        this.toolManager = toolManager;
        diagramRenderer = new DiagramRenderer();
        editorController = new EditorController(editorModel, toolManager, stateChangedCallback, this);

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(900, 700));

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                editorController.handleMousePressed(event);
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                editorController.handleMouseDragged(event);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                editorController.handleMouseReleased(event);
            }

            @Override
            public void mouseMoved(MouseEvent event) {
                editorController.handleMouseMoved(event);
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public void chooseTool(ToolType toolType) {
        editorController.chooseTool(toolType);
    }

    public ToolType getCurrentToolType() {
        return toolManager.getCurrentToolType();
    }

    public EditorModel getEditorModel() {
        return editorModel;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2d = (Graphics2D) graphics;
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        diagramRenderer.drawModel(graphics2d, editorModel);
        diagramRenderer.drawInteractionOverlays(graphics2d, toolManager, editorController);
        drawStatusText(graphics2d);
    }

    private void drawStatusText(Graphics2D graphics2d) {
        graphics2d.setColor(Color.BLACK);
        graphics2d.drawString("Canvas", 12, 20);
        graphics2d.drawString("Current Tool: " + toolManager.getCurrentToolType(), 12, 38);
    }
}
