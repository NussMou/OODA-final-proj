import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class EditorFrame extends JFrame {
    private final ToolManager toolManager;
    private final EditorModel editorModel;
    private final ToolbarPanel toolbarPanel;
    private final EditorCanvas editorCanvas;

    public EditorFrame() {
        super("UML Editor");
        toolManager = new ToolManager();
        editorModel = new EditorModel();
        toolbarPanel = new ToolbarPanel(this::handleToolChosen);
        editorCanvas = new EditorCanvas(editorModel, toolManager,
                () -> toolbarPanel.updateHighlightedTool(toolManager.getCurrentToolType()));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setJMenuBar(buildMenuBar());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(toolbarPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(editorCanvas, BorderLayout.CENTER);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem groupItem = new JMenuItem(new javax.swing.AbstractAction("Group") {
            @Override
            public void actionPerformed(ActionEvent event) {
                editorModel.groupSelectedNodes();
                editorCanvas.repaint();
            }
        });
        JMenuItem ungroupItem = new JMenuItem(new javax.swing.AbstractAction("Ungroup") {
            @Override
            public void actionPerformed(ActionEvent event) {
                editorModel.ungroupSelectedNode();
                editorCanvas.repaint();
            }
        });
        JMenuItem labelItem = new JMenuItem(new javax.swing.AbstractAction("Label") {
            @Override
            public void actionPerformed(ActionEvent event) {
                showLabelDialog();
            }
        });
        editMenu.add(groupItem);
        editMenu.add(ungroupItem);
        editMenu.add(labelItem);
        menuBar.add(editMenu);

        return menuBar;
    }

    private void handleToolChosen(ToolType toolType) {
        editorCanvas.chooseTool(toolType);
        toolbarPanel.updateHighlightedTool(toolManager.getCurrentToolType());
    }

    private void showLabelDialog() {
        List<Node> selectedNodes = editorModel.getSelectedNodeList();
        if (selectedNodes.size() != 1 || !(selectedNodes.get(0) instanceof BasicNode)) {
            JOptionPane.showMessageDialog(this,
                    "Please select exactly one basic object first.",
                    "Label",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        BasicNode basicNode = (BasicNode) selectedNodes.get(0);
        LabelDialog labelDialog = new LabelDialog(this, basicNode);
        labelDialog.setVisible(true);

        if (labelDialog.isConfirmed()) {
            basicNode.setLabelName(labelDialog.getLabelName());
            basicNode.setLabelColor(labelDialog.getSelectedColor());
            editorCanvas.repaint();
        }
    }
}
