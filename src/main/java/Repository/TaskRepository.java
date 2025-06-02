/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Repository;

import Helpers.Helpers;
import Model.Task;
import Model.TaskCategory;
import Model.TaskUtils.TaskPriority;
import Model.TaskUtils.TaskStatus;
import Validator.Validator;
import database.Db;
import java.sql.Connection;
import java.sql.Date;
//import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 *
 * @author jeanc
 */
public class TaskRepository {

    private Db db = new Db();
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Helpers helper = new Helpers();
    private Connection connection = db.getConnection();
    private Map<Integer, TaskCategory> categories;
    private ScheduledExecutorService executorService;
    private List<Task> tasks;

    public TaskRepository() {
        connection = db.getConnection();

    }

    public void addTask(Task task) {
        if (Validator.validateTaskForm(task.getTitle(), task.getDescription(), task.getDueDate(), task.getPriority(), task.getStatus())) {
            String query = "INSERT INTO tasks(title, description, dueDate, status, priority, category_id) "
                    + "VALUES (?,?,?,?,?,?)";
            try (PreparedStatement taskStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                taskStatement.setString(1, task.getTitle());
                taskStatement.setString(2, task.getDescription());
                taskStatement.setDate(3, Date.valueOf(task.getDueDate()));
                taskStatement.setString(4, task.getStatus().name());
                taskStatement.setString(5, task.getPriority().name());
                taskStatement.setInt(6, task.getCategory().getId());
                taskStatement.executeUpdate();

            } catch (SQLException e) {
                helper.alert("Enregistrement échoué", " " + e, "error");
            }
        } else {

        }
    }

    public void updateTask(Task task) {
        if (Validator.validateTaskForm(task.getTitle(), task.getDescription(), task.getDueDate(), task.getPriority(), task.getStatus())) {
            String query = "UPDATE tasks SET title = ?, description =?, dueDate = ?, status = ?, priority = ?, category_id = ? WHERE id = ? ";

            try (PreparedStatement taskStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                taskStatement.setString(1, task.getTitle());
                taskStatement.setString(2, task.getDescription());
                taskStatement.setDate(3, Date.valueOf(task.getDueDate()));
                taskStatement.setString(4, task.getStatus().name());
                taskStatement.setString(5, task.getPriority().name());
                taskStatement.setInt(6, task.getCategory().getId());
                taskStatement.setInt(7, task.getId());
                taskStatement.executeUpdate();

            } catch (SQLException e) {
                helper.alert("Modification échouée", " " + e, "error");
            }
        }
    }

    public void deleteTask(Task task) {

        String query = "DELETE FROM tasks WHERE Id = ?";

        try (PreparedStatement taskStatement = connection.prepareStatement(query)) {
            taskStatement.setInt(1, task.getId());
            taskStatement.executeUpdate();

        } catch (SQLException e) {
            helper.alert("Enregistrement échoué", " " + e, "error");
        }
    }

    public List<Task> getAllTasks() {
        String query = "SELECT * FROM tasks";
        List<Task> tasks = new ArrayList<>();

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDate dueDate = resultSet.getDate("dueDate").toLocalDate();
                TaskPriority priority = TaskPriority.valueOf(resultSet.getString("priority"));
                int categoryId = resultSet.getInt("category_id");
                TaskCategory category = getTaskCategory(categoryId);
                TaskStatus status = TaskStatus.valueOf(resultSet.getString("status"));
                Task task = new Task(id, title, description, dueDate, status, priority, category);

                tasks.add(task);
            }
        } catch (SQLException e) {
            logger.info(e.toString());
        }

        return tasks;
    }

    public TaskCategory getTaskCategory(int categoryId) {
        String query = "SELECT * FROM categories WHERE id = ?";
        try (PreparedStatement taskStatement = connection.prepareStatement(query)) {

            taskStatement.setInt(1, categoryId);
            try (ResultSet resultSet = taskStatement.executeQuery()) {
                String name = resultSet.getString("name");
                return new TaskCategory(categoryId, name);
            } catch (SQLException e) {

            }
            taskStatement.executeUpdate();

        } catch (SQLException e) {
            helper.alert("Impossible de récupérer échouée", " " + e, "error");
        }
        return null;
    }

    public Task getTask(int taskId) {
        String query = "SELECT * FROM tasks WHERE id = ?";
        try (PreparedStatement taskStatement = connection.prepareStatement(query)) {

            taskStatement.setInt(1, taskId);
            try (ResultSet resultSet = taskStatement.executeQuery()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDate dueDate = resultSet.getDate("dueDate").toLocalDate();
                TaskPriority priority = TaskPriority.valueOf(resultSet.getString("priority"));
                int categoryId = resultSet.getInt("category_id");
                TaskCategory category = getTaskCategory(categoryId);
                TaskStatus status = TaskStatus.valueOf(resultSet.getString("status"));
                return new Task(id, title, description, dueDate, status, priority, category);

            } catch (SQLException e) {

            }
            taskStatement.executeUpdate();

        } catch (SQLException e) {
            helper.alert("Impossible de récupérer échouée", " " + e, "error");
        }
        return null;
    }

    public List<Task> getTasksDueSoon() {
        String query = "SELECT * FROM tasks WHERE dueDate BETWEEN ? AND ? AND status != 'COMPLETED'";
        List<Task> tasks = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(today));
            statement.setDate(2, Date.valueOf(tomorrow));

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDate dueDate = resultSet.getDate("dueDate").toLocalDate();
                TaskPriority priority = TaskPriority.valueOf(resultSet.getString("priority"));
                int categoryId = resultSet.getInt("category_id");
                TaskCategory category = getTaskCategory(categoryId);
                TaskStatus status = TaskStatus.valueOf(resultSet.getString("status"));
                Task task = new Task(id, title, description, dueDate, status, priority, category);
                tasks.add(task);
            }
        } catch (SQLException e) {
            logger.info(e.toString());
        }

        return tasks;
    }

    public List<Task> getOverdueTasks() {
        String query = "SELECT * FROM tasks WHERE dueDate < ? AND status != 'COMPLETED'";
        List<Task> tasks = new ArrayList<>();
        LocalDate today = LocalDate.now();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(today));

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDate dueDate = resultSet.getDate("dueDate").toLocalDate();
                TaskPriority priority = TaskPriority.valueOf(resultSet.getString("priority"));
                int categoryId = resultSet.getInt("category_id");
                TaskCategory category = getTaskCategory(categoryId);
                TaskStatus status = TaskStatus.valueOf(resultSet.getString("status"));
                Task task = new Task(id, title, description, dueDate, status, priority, category);
                tasks.add(task);
            }
        } catch (SQLException e) {
            logger.info(e.toString());
        }

        return tasks;
    }

}
