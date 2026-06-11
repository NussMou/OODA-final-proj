import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditorModel {
    private final List<Node> topLevelNodes;
    private final List<Link> links;
    private final Set<Node> selectedNodes;
    private Node hoveredNode;
    private int nextFrontDepth;

    public EditorModel() {
        topLevelNodes = new ArrayList<>();
        links = new ArrayList<>();
        selectedNodes = new HashSet<>();
        hoveredNode = null;
        nextFrontDepth = -1;
    }

    public List<Node> getTopLevelNodesInDrawOrder() {
        List<Node> orderedNodes = new ArrayList<>(topLevelNodes);
        orderedNodes.sort(Comparator.comparingInt(Node::getDepth).reversed());
        return orderedNodes;
    }

    public List<Link> getLinksInDrawOrder() {
        List<Link> orderedLinks = new ArrayList<>(links);
        orderedLinks.sort(Comparator.comparingInt(Link::getDepth).reversed());
        return orderedLinks;
    }

    public void addNode(Node node) {
        node.setDepth(nextFrontDepth--);
        topLevelNodes.add(node);
    }

    public void addLink(Link link) {
        link.setDepth(nextFrontDepth--);
        links.add(link);
    }

    public int consumeFrontDepth() {
        return nextFrontDepth--;
    }

    public Node findTopMostNode(Point point) {
        Node resultNode = null;
        for (Node node : topLevelNodes) {
            if (node.contains(point)) {
                if (resultNode == null || node.getDepth() < resultNode.getDepth()) {
                    resultNode = node;
                }
            }
        }
        return resultNode;
    }

    public BasicNode findTopMostBasicNodeContaining(Point point) {
        Node topNode = findTopMostNode(point);
        if (topNode == null) {
            return null;
        }
        return topNode.findBasicNodeContaining(point);
    }

    public Port findPortAt(Point point) {
        BasicNode basicNode = findTopMostBasicNodeContaining(point);
        if (basicNode == null) {
            return null;
        }
        return basicNode.getPortAt(point);
    }

    public void clearHovered() {
        if (hoveredNode != null) {
            hoveredNode.setHovered(false);
        }
        hoveredNode = null;
    }

    public void updateHover(Point point) {
        Node newHoveredNode = findTopMostNode(point);
        if (hoveredNode == newHoveredNode) {
            return;
        }
        if (hoveredNode != null) {
            hoveredNode.setHovered(false);
        }
        hoveredNode = newHoveredNode;
        if (hoveredNode != null) {
            hoveredNode.setHovered(true);
        }
    }

    public Node getHoveredNode() {
        return hoveredNode;
    }

    public Set<Node> getSelectedNodes() {
        return selectedNodes;
    }

    public List<Node> getSelectedNodeList() {
        return new ArrayList<>(selectedNodes);
    }

    public void clearSelection() {
        for (Node node : selectedNodes) {
            node.setSelected(false);
        }
        selectedNodes.clear();
    }

    public void setSingleSelection(Node node) {
        clearSelection();
        if (node != null) {
            selectedNodes.add(node);
            node.setSelected(true);
            bringToFront(node);
        }
    }

    public void setMultiSelection(List<Node> nodes) {
        clearSelection();
        for (Node node : nodes) {
            selectedNodes.add(node);
            node.setSelected(true);
            bringToFront(node);
        }
    }

    public void bringToFront(Node node) {
        if (node != null) {
            node.setDepth(nextFrontDepth--);
        }
    }

    public List<Node> findNodesFullyInside(Rectangle selectionBounds) {
        List<Node> insideNodes = new ArrayList<>();
        for (Node node : topLevelNodes) {
            if (selectionBounds.contains(node.getBounds())) {
                insideNodes.add(node);
            }
        }
        return insideNodes;
    }

    public boolean canGroupSelectedNodes() {
        return selectedNodes.size() >= 2;
    }

    public void groupSelectedNodes() {
        if (!canGroupSelectedNodes()) {
            return;
        }

        List<Node> groupedNodes = getSelectedNodeList();
        topLevelNodes.removeAll(groupedNodes);
        CompositeNode compositeNode = new CompositeNode(groupedNodes, consumeFrontDepth());
        topLevelNodes.add(compositeNode);
        setSingleSelection(compositeNode);
    }

    public boolean canUngroupSelectedNode() {
        return selectedNodes.size() == 1 && getSelectedNodeList().get(0) instanceof CompositeNode;
    }

    public void ungroupSelectedNode() {
        if (!canUngroupSelectedNode()) {
            return;
        }

        CompositeNode compositeNode = (CompositeNode) getSelectedNodeList().get(0);
        topLevelNodes.remove(compositeNode);
        List<Node> children = new ArrayList<>(compositeNode.getChildren());
        topLevelNodes.addAll(children);
        setMultiSelection(children);
    }
}
