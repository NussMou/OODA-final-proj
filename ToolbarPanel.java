import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ToolbarPanel extends JPanel {
    private final Map<ToolType, JButton> buttonMap;

    public ToolbarPanel(Consumer<ToolType> onToolChosen) {
        buttonMap = new EnumMap<>(ToolType.class);
        setLayout(new GridLayout(6, 1, 0, 6));
        setPreferredSize(new Dimension(140, 420));

        addButton("Select", ToolType.SELECT, onToolChosen);
        addButton("Association", ToolType.ASSOCIATION, onToolChosen);
        addButton("Generalization", ToolType.GENERALIZATION, onToolChosen);
        addButton("Composition", ToolType.COMPOSITION, onToolChosen);
        addButton("Rect", ToolType.RECT, onToolChosen);
        addButton("Oval", ToolType.OVAL, onToolChosen);

        updateHighlightedTool(ToolType.SELECT);
    }

    private void addButton(String text, ToolType toolType, Consumer<ToolType> onToolChosen) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.addActionListener(e -> onToolChosen.accept(toolType));
        buttonMap.put(toolType, button);
        add(button);
    }

    public void updateHighlightedTool(ToolType activeToolType) {
        for (Map.Entry<ToolType, JButton> entry : buttonMap.entrySet()) {
            JButton button = entry.getValue();
            if (entry.getKey() == activeToolType) {
                button.setBackground(Color.BLACK);
                button.setForeground(Color.RED);
            } else {
                button.setBackground(null);
                button.setForeground(Color.BLACK);
            }
        }
    }
}
