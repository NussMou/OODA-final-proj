import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BasicNode extends Node {
    protected String labelName;
    protected Color labelColor;
    private final List<Port> ports;

    protected BasicNode(int x, int y, int width, int height, int depth) {
        super(x, y, width, height, depth);
        labelName = "";
        labelColor = new Color(255, 255, 210);
        ports = new ArrayList<>();
        initializePorts();
    }

    private void initializePorts() {
        int portCount = getPortCount();
        for (int i = 0; i < portCount; ++i) {
            ports.add(new Port(this, i));
        }
    }

    public abstract int getPortCount();

    public abstract Point getPortCenterPoint(int index);

    public List<Port> getPorts() {
        return Collections.unmodifiableList(ports);
    }

    public Port getPortAt(Point point) {
        for (Port port : ports) {
            if (port.contains(point)) {
                return port;
            }
        }
        return null;
    }

    @Override
    public boolean isBasicNode() {
        return true;
    }

    @Override
    public boolean isCompositeNode() {
        return false;
    }

    @Override
    public BasicNode findBasicNodeContaining(Point point) {
        return contains(point) ? this : null;
    }

    public String getLabelName() {
        return labelName;
    }

    public Color getLabelColor() {
        return labelColor;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName == null ? "" : labelName;
    }

    public void setLabelColor(Color labelColor) {
        if (labelColor != null) {
            this.labelColor = labelColor;
        }
    }

    public abstract void resizeFromPort(int portIndex, Point draggedPoint);
}
