package model;

import manager.enums.StatusTask;
import manager.enums.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();

    public Epic(String name, String description, int id, StatusTask status, TypeTask typeTask) {
        super(name, description, id, status, typeTask, null, 0);
    }

    private void startTimeEpic(Integer inputId, String operation) {
        boolean isClearAll = inputId == null && operation.equals("ZERO");
        if (isClearAll) {
            this.startTime = null;
            return;
        }
        Subtask subtask = subtaskHashMap.get(inputId);

        LocalDateTime startTime = subtask.getStartTime();
        switch (operation) {
            case "+":
                if (this.startTime == null) {
                    this.startTime = startTime;
                } else if (startTime.isBefore(this.startTime)) {
                    this.startTime = startTime;
                }

                break;
            case "-":
                if (startTime == this.startTime) {
                    this.startTime = returnNewStartTime();
                }
                break;
        }
    }

    private LocalDateTime returnNewStartTime() {
        LocalDateTime newStartTime = null;
        for (Subtask subtask : subtaskHashMap.values()) {
            LocalDateTime startTimeSubtask = subtask.getStartTime();
            if (this.startTime == null) {
                return startTimeSubtask;
            }

            if (startTimeSubtask.isBefore(this.startTime)) {
                newStartTime = startTimeSubtask;
            }
        }
        return newStartTime;
    }

    private void durationEpic(Integer inputId, String operation) {
        boolean isClearAll = inputId == null && operation.equals("ZERO");
        if (isClearAll) {
            this.duration = Duration.ZERO;
            return;
        }
        Subtask subtask = subtaskHashMap.get(inputId);
        Duration durationSubtask = subtask.getDuration();
        switch (operation) {
            case "+":
                this.duration = this.duration.plus(durationSubtask);
                break;
            case "-":
                this.duration = this.duration.minus(durationSubtask);
                break;
        }
    }

    public void addSubtask(Subtask subtask) {
        int id = subtask.getId();
        subtaskHashMap.put(id, subtask);
        startTimeEpic(id, "+");
        durationEpic(id, "+");
    }

    public void removeSubtaskById(int id) {
        startTimeEpic(id, "-");
        durationEpic(id, "-");
        subtaskHashMap.remove(id);

    }

    public void clearSubtasks() {
        subtaskHashMap.clear();
        startTimeEpic(null, "ZERO");
        durationEpic(null, "ZERO");
    }

    public HashMap<Integer, Subtask> getSubtasksMap() {
        return subtaskHashMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskHashMap, epic.subtaskHashMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskHashMap);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name +
                ", description='" + description +
                ", type=" + type +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime + '\'' +
                ", duration=" + duration + '\'' +
                '}';
    }
}
