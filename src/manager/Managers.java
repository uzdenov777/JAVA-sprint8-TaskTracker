package manager;

import manager.interfaces.HistoryManager;
import manager.interfaces.PrioritizedManager;
import manager.interfaces.TaskManager;

public class Managers {

    public static HttpTaskManager getDefault(String kvServerUrl) {
        return new HttpTaskManager(kvServerUrl);
    }

    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTasksManager(String saveFile) {
        return new FileBackedTasksManager(saveFile);
    }

    public static PrioritizedManager getDefaultPrioritizedManager() {
        return new InMemoryPrioritizedManager();
    }
}
