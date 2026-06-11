public enum ToolType {
    SELECT,
    ASSOCIATION,
    GENERALIZATION,
    COMPOSITION,
    RECT,
    OVAL;

    public boolean isLinkTool() {
        return this == ASSOCIATION || this == GENERALIZATION || this == COMPOSITION;
    }

    public boolean isCreateTool() {
        return this == RECT || this == OVAL;
    }
}
