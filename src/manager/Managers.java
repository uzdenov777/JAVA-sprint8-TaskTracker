package manager;

import manager.interfaces.HistoryManager;
import manager.interfaces.PrioritizedManager;
import manager.interfaces.TaskManager;

import java.io.File;

public class Managers {

    public static HttpTaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8080");
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
