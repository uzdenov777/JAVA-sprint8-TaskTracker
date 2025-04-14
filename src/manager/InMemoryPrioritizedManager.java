package manager;

import manager.enums.TypeTask;
import manager.interfaces.PrioritizedManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class InMemoryPrioritizedManager implements PrioritizedManager {
    private final TreeMap<LocalDateTime, Task> tasksByPriority = new TreeMap<>();
    private final List<Task> nullDateTasks = new ArrayList<>();

    public boolean addTaskWithoutIntersection(Task task) {
        LocalDateTime startOutTask = task.getStartTime();
        LocalDateTime endOutTask = task.getEndTime();


        if (startOutTask == null) {//Null может быть только у Epic-a, у которого нет подзадач, такие Epic-и храним в отдельном списке.
            nullDateTasks.add(task);
            return true;
        }

        if (tasksByPriority.isEmpty()) { //Если не добавлено еще ни одной задачи можно ничего не проверять и вставлять
            tasksByPriority.put(startOutTask, task);
            return true;
        }

        // Проверка на пересечение с уже существующей задачей (по времени)
        if (tasksByPriority.containsKey(startOutTask)) {
            System.out.println("Дата начала задачи уже занята!");
            return false;
        }

        // Проверка, если задача должна быть вставлена перед самой первой задачей
        LocalDateTime before = tasksByPriority.lowerKey(startOutTask);
        if (before == null) {
            LocalDateTime first = tasksByPriority.firstKey();
            if (endOutTask.isBefore(first)) {
                tasksByPriority.put(startOutTask, task);
                return true;
            }else {
                System.out.println("Задача которую хотите добавить заканчивается позже чем начало следующей");
                return false;
            }
        }

        Task previousTask = tasksByPriority.get(before);
        LocalDateTime previousTaskEndTime = previousTask.getEndTime();

        // Если задача начинается после окончания предыдущей
        if (startOutTask.isAfter(previousTaskEndTime)) {
            LocalDateTime nextTaskStartTime = tasksByPriority.higherKey(startOutTask);
            // Если следующей задачи нет
            if (nextTaskStartTime == null) {
                tasksByPriority.put(startOutTask, task);
                return true;
            }

            //Задача заканчивается до начала следующей задачи
            if (endOutTask.isBefore(nextTaskStartTime)) {
                tasksByPriority.put(startOutTask, task);
                return true;
            } else {
                System.out.println("Задача, которую вы хотите добавить не закончена вовремя.");
                return false;
            }
        } else {
            System.out.println("Задача, которую вы хотите добавить начинается до окончания предыдущей задачи.");
            return false;
        }
    }

    public boolean updateTaskWithoutIntersection(Task newTask, Task oldTask) {
        removeTaskFromPrioritizedAndNullLists(oldTask);// Удаляем старую чтобы не мешалась

        boolean addTask = addTaskWithoutIntersection(newTask);// Пробуем добавить новую задачу
        if (addTask) {
            return true;
        } else {
            addTaskWithoutIntersection(oldTask);// Если не получилось добавить новую возвращаем старую на свое место.
            return false;
        }
    }

    public void updateEpicWithoutIntersection(Epic newEpic, Epic oldEpic) {
        LocalDateTime newStartTime = newEpic.getStartTime();
        LocalDateTime oldStartTime = oldEpic.getStartTime();

        if (oldStartTime == null) { // если oldStartTime не null значит эпик нигде не храниться как отдельная задача во временном диапазоне
            nullDateTasks.remove(oldEpic);
        }

        if (newStartTime == null) {
         // значит нет подзадач у эпика и добавляем в nullDateTasks.
            addTaskWithoutIntersection(newEpic);
        }
    }

    public boolean updateSubtaskWithoutIntersection(Subtask newSubtask, Subtask oldTSubtask) {
        removeTaskFromPrioritizedAndNullLists(oldTSubtask);// Удаляем старую чтобы не мешалась

        boolean isAddTask = addTaskWithoutIntersection(newSubtask);// Пробуем добавить новую задачу
        if (isAddTask) {
            return true;
        } else {
            addTaskWithoutIntersection(oldTSubtask);// Если не получилось добавить новую возвращаем старую на свое место.
            return false;
        }
    }

    public void removeTaskFromPrioritizedAndNullLists(Task task) {
        TypeTask type = task.getType();

        boolean isEpic = type == TypeTask.EPIC;
        // Скорее всего это Epic, у которого нет подзадач и поэтому находиться в nullDateTasks.
        if (isEpic) { // либо у него появилась подзадача и теперь не должен быть в nullData
            nullDateTasks.remove(task);
        } else {
            LocalDateTime startOutTask = task.getStartTime();
            tasksByPriority.remove(startOutTask);
        }
    }

    public void clearAllTasksFromPrioritizedAndNullLists(Map<Integer, Task> inputTasksMap) {
        for (Task task : inputTasksMap.values()) {
            LocalDateTime startOutTask = task.getStartTime();
            tasksByPriority.remove(startOutTask);
        }
    }

    public void clearAllEpicsFromPrioritizedAndNullLists(Map<Integer, Epic> inputTasksMap) {
        for (Epic epic : inputTasksMap.values()) {
            LocalDateTime startOutTask = epic.getStartTime();
            if (startOutTask == null) { // Скорее всего это Epic, у которого нет подзадач и поэтому находиться в nullDateTasks.
                nullDateTasks.remove(epic);
            }
        }
    }

    public void clearAllSubtasksFromPrioritizedAndNullLists(Map<Integer, Subtask> inputTasksMap) {
        for (Subtask subtask : inputTasksMap.values()) {
            LocalDateTime startOutTask = subtask.getStartTime();
            tasksByPriority.remove(startOutTask);
        }
    }

    public List<Task> getPrioritizedTasks() {
        List<Task> prioritizedTasks = new ArrayList<>(tasksByPriority.values());
        prioritizedTasks.addAll(nullDateTasks);
        return prioritizedTasks;
    }
}
