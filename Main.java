import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EditorFrame editorFrame = new EditorFrame();
            editorFrame.setVisible(true);
        });
    }
}
