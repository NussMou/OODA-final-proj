import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LabelDialog extends JDialog {
    private final JTextField nameField;
    private final JButton colorButton;
    private Color selectedColor;
    private boolean confirmed;

    public LabelDialog(Frame owner, BasicNode basicNode) {
        super(owner, "Customize Label Style", true);
        nameField = new JTextField(basicNode.getLabelName(), 16);
        selectedColor = basicNode.getLabelColor();
        confirmed = false;

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        formPanel.add(new JLabel("Name"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Color"));
        colorButton = new JButton("Choose...");
        colorButton.setBackground(selectedColor);
        colorButton.addActionListener(e -> chooseColor());
        formPanel.add(colorButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        setLayout(new BorderLayout(10, 10));
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }

    private void chooseColor() {
        Color newColor = JColorChooser.showDialog(this, "Choose Label Color", selectedColor);
        if (newColor != null) {
            selectedColor = newColor;
            colorButton.setBackground(selectedColor);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getLabelName() {
        return nameField.getText();
    }

    public Color getSelectedColor() {
        return selectedColor;
    }
}
