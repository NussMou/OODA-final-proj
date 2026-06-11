public class ToolManager {
    private ToolType currentToolType;
    private ToolType persistentToolType;
    private ToolType previousPersistentToolType;

    public ToolManager() {
        currentToolType = ToolType.SELECT;
        persistentToolType = ToolType.SELECT;
        previousPersistentToolType = ToolType.SELECT;
    }

    public ToolType getCurrentToolType() {
        return currentToolType;
    }

    public ToolType getPersistentToolType() {
        return persistentToolType;
    }

    public void setPersistentTool(ToolType toolType) {
        if (toolType == null) {
            return;
        }
        previousPersistentToolType = persistentToolType;
        persistentToolType = toolType;
        currentToolType = toolType;
    }

    public void beginTemporaryCreateTool(ToolType toolType) {
        if (toolType == null || !toolType.isCreateTool()) {
            return;
        }
        previousPersistentToolType = persistentToolType;
        currentToolType = toolType;
    }

    public void finishTemporaryCreateTool() {
        if (currentToolType.isCreateTool()) {
            currentToolType = previousPersistentToolType;
        }
    }
}
