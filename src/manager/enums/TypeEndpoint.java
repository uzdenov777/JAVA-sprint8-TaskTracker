package manager.enums;

import com.sun.net.httpserver.HttpExchange;

import java.net.URI;

public enum TypeEndpoint {
    GET_ALL_TASKS,
    GET_ALL_EPICS,
    GET_ALL_SUBTASKS,
    GET_TASK_BY_ID,
    GET_EPIC_BY_ID,
    GET_SUBTASK_BY_ID,
    POST_ADD_TASK,
    POST_ADD_EPIC,
    POST_ADD_SUBTASK,
    DELETE_TASK_BY_ID,
    DELETE_EPIC_BY_ID,
    DELETE_SUBTASK_BY_ID,
    DELETE_ALL_TASKS,
    DELETE_ALL_EPICS,
    DELETE_ALL_SUBTASKS,
    GET_EPIC_SUBTASKS,
    GET_HISTORY,
    GET_Prioritized_TASKS,
    UNKNOWN;

    public static TypeEndpoint getEndpoint(HttpExchange h) {
        URI requestURI = h.getRequestURI();
        String requestMethod = h.getRequestMethod();
        String query = requestURI.getQuery();
        String[] requestPath = requestURI.getPath().split("/");
        String[] requestQuery = query.split("&");

        switch (requestMethod) {
            case "GET":
                return returnGetEndpoint(requestPath, requestQuery);
            case "POST":
                return returnPostEndpoint(requestPath, requestQuery);
            case "DELETE":
                return returnDeleteEndpoint(requestPath, requestQuery);
            default:
                return UNKNOWN;
        }
    }

    private static TypeEndpoint returnGetEndpoint(String[] requestPath, String[] requestQuery) {
        int lengthPath = requestPath.length;
        int lengthQuery = requestQuery.length;

        if (lengthPath < 2) {
            System.out.println("Неверный URI запроса для метода GET");
            return UNKNOWN;
        }

        if (lengthQuery == 1) {
            return returnGetAllEndpoint(requestPath);
        } else if (lengthQuery == 2) {
            return returnGetByIdEndpoint(requestPath);
        } else {
            return UNKNOWN;
        }
    }

    private static TypeEndpoint returnGetAllEndpoint(String[] requestPath) {
        int lengthPath = requestPath.length;
        if (lengthPath == 2) {
            return GET_Prioritized_TASKS;
        }

        String typeTask = requestPath[2];
        if (lengthPath == 3) {
            switch (typeTask) {
                case "task":
                    return GET_ALL_TASKS;
                case "epic":
                    return GET_ALL_EPICS;
                case "subtask":
                    return GET_ALL_SUBTASKS;
                case "history":
                    return GET_HISTORY;
                default:
                    return UNKNOWN;
            }
        }
        return UNKNOWN;
    }

    private static TypeEndpoint returnGetByIdEndpoint(String[] requestPath) {
        int lengthPath = requestPath.length;
        String typeTask = requestPath[2];

        if (lengthPath == 3) {
            switch (typeTask) {
                case "task":
                    return GET_TASK_BY_ID;
                case "epic":
                    return GET_EPIC_BY_ID;
                case "subtask":
                    return GET_SUBTASK_BY_ID;
                default:
                    return UNKNOWN;
            }
        }

        boolean isLenghtPath = requestPath.length == 4;
        boolean isPatchSubtask_Epic = (requestPath[2].equals("epic")) && (requestPath[3].equals("subtask"));
        if (isLenghtPath && isPatchSubtask_Epic) {
            return GET_EPIC_SUBTASKS;
        }

        return UNKNOWN;
    }

    private static TypeEndpoint returnPostEndpoint(String[] requestPath, String[] requestQuery) {

        if (requestPath.length != 3 || requestQuery.length > 1) {
            System.out.println("Неверный URI запроса для метода POST");
            return UNKNOWN;
        }

        String typeTask = requestPath[2];
        switch (typeTask) {
            case "task":
                return POST_ADD_TASK;
            case "epic":
                return POST_ADD_EPIC;
            case "subtask":
                return POST_ADD_SUBTASK;
            default:
                return UNKNOWN;
        }
    }

    private static TypeEndpoint returnDeleteEndpoint(String[] requestPath, String[] requestQuery) {
        if (requestPath.length != 3) {
            System.out.println("Неверный URI запроса для метода DELETE");
            return UNKNOWN;
        }

        String typeTask = requestPath[2];
        if (requestQuery.length == 1) {//если в параметрах только API_TOKEN, значит надо удалить всё задачи определенного типа
            switch (typeTask) {
                case "task":
                    return DELETE_ALL_TASKS;
                case "epic":
                    return DELETE_ALL_EPICS;
                case "subtask":
                    return DELETE_ALL_SUBTASKS;
                default:
                    return UNKNOWN;
            }
        } else if (requestQuery.length == 2) {//если в параметрах API_TOKEN и ID для удаления определенной задачи
            switch (typeTask) {
                case "task":
                    return DELETE_TASK_BY_ID;
                case "epic":
                    return DELETE_EPIC_BY_ID;
                case "subtask":
                    return DELETE_SUBTASK_BY_ID;
                default:
                    return UNKNOWN;
            }
        } else {
            return UNKNOWN;
        }
    }
}
