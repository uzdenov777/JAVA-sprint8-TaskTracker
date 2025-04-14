package model;

import manager.enums.StatusTask;
import manager.enums.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class Task {
    protected final String name;
    protected final String description;
    protected StatusTask status;
    protected final int id;
    protected final TypeTask type;


    protected LocalDateTime startTime;
    protected Duration duration;
    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public Task(String name, String description, int id, StatusTask status, TypeTask type, String startTime, long duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.type = type;
        this.duration = getValidDuration(duration, type);
        this.startTime = getValidDateTime(startTime, type);
    }

    protected LocalDateTime getValidDateTime(String startTime, TypeTask type) {
        LocalDateTime result = null;
        try {
            if (type != TypeTask.EPIC) {
                result = LocalDateTime.parse(startTime, formatter);
            }
        } catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException("Передан неверный формат даты или null для Task и Subtask. Валидный формат для Task, Subtask -> ДД.ММ.ГГГГ ЧЧ:ММ");
        }
        return result;
    }

    private static Duration getValidDuration(long duration, TypeTask type) {
        boolean notEpic = type != TypeTask.EPIC;
        boolean notZeroAndNotNegative = duration <= 0;
        if (notEpic && notZeroAndNotNegative) {
            throw new IllegalArgumentException("Продолжительность задачи принимается от 1 минуты.");
        }
        return Duration.ofMinutes(duration);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TypeTask getType() {
        return type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    public String getStartTimeToString() {
        if (startTime == null) {
            return null;
        }
        return startTime.format(formatter);
    }

    public String getEndTimeToString() {
        if (startTime == null) {
            return null;
        }
        return startTime.plus(duration).format(formatter);
    }

    public long getDurationToLong() {
        return duration.toMinutes();
    }

    public int getId() {
        return id;
    }

    public StatusTask getStatus() {
        return status;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name +
                ", description='" + description +
                ", type=" + type +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime + '\'' +
                ", duration=" + duration + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id);
    }
}