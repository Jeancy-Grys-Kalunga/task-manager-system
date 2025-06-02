/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jeancygrys.taskmanagerapp;

import Helpers.Helpers;
import Model.Task;
import Model.TaskUtils.TaskStatus;
import Repository.TaskRepository;
import TableController.TaskTableModel;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class TaskTableController {

    @FXML
    public TableView<TaskTableModel> taskTableView;
    public ObservableList<TaskTableModel> taskItems = FXCollections.observableArrayList();
    public TaskRepository taskRepository = new TaskRepository();
    private Helpers helper = new Helpers();
    private Timeline notificationTimeline;

    @FXML
    private Button getAllTasksCategoriesView;
    @FXML
    private Button getSettingsButton;
    @FXML
    private Button getCompletedTasksButton;
    @FXML
    private Button inProcessButton;
    @FXML
    private Button getViewAddTaskButton;
    @FXML
    private Button getViewAddCategoryTaskButton;
    @FXML
    private Button InLatestBtn;
    @FXML
    private TextField search_task_TextField;
    @FXML
    private Button deleteTaskBtn;
    @FXML
    private Button getViewAddTaskButton1112;
    @FXML
    private Button getEditTaskView;

    @FXML
    private Label completedCountLabel;
    @FXML
    private Label inProgressCountLabel;
    @FXML
    private Label lateCountLabel;
    @FXML
    private Label notificationBadge;

    public void initialize() {
        initializeColumns();
        refreshData();
        startNotificationChecker();

        // Appliquer le filtre si nécessaire
        if (App.currentTaskFilter != null) {
            switch (App.currentTaskFilter) {
                case "COMPLETED":
                    getCompletedTasks(null);
                    break;
                case "IN_PROGRESS":
                    getInProcessTasks(null);
                    break;
                case "LATE":
                    getLateTasks(null);
                    break;
            }
            App.currentTaskFilter = null; // Réinitialiser
        }
    }

    private void startNotificationChecker() {
        notificationTimeline = new Timeline(
                new KeyFrame(Duration.minutes(1), event -> {
                    refreshData();
                    checkForNewNotifications();
                }));
        notificationTimeline.setCycleCount(Timeline.INDEFINITE);
        notificationTimeline.play();
    }

    private void checkForNewNotifications() {
        int currentLateCount = Integer.parseInt(lateCountLabel.getText());
        int actualLateCount = calculateLateCount();

        if (actualLateCount > currentLateCount) {
            Platform.runLater(() -> {
                notificationBadge.setText(String.valueOf(actualLateCount));
                notificationBadge.setVisible(true);

                // Optionnel: Afficher une notification toast
                helper.alert("Nouvelles tâches en retard", "Nouvelles tâches en retard détectées!", "success");

            });
        }
    }

    private int calculateLateCount() {
        int lateCount = 0;
        LocalDate today = LocalDate.now();

        for (TaskTableModel task : taskItems) {
            String status = task.getStatus();
            try {
                LocalDate dueDate = LocalDate.parse(task.getDueDate());
                if (dueDate.isBefore(today) && !status.equals(TaskStatus.DONE.toString())) {
                    lateCount++;
                }
            } catch (Exception e) {
                // Gérer l'exception
            }
        }
        return lateCount;
    }

    private String translateStatus(String status) {
        switch (status) {
            case "DONE":
                return "Terminée";
            case "IN_PROGRESS":
                return "En cours";
            case "TODO":
                return "À faire";
            default:
                return status;
        }
    }

    private void initializeColumns() {
        taskTableView.getColumns().clear();

        TableColumn<TaskTableModel, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        idColumn.setPrefWidth(60);

        TableColumn<TaskTableModel, String> nameColumn = new TableColumn<>("Nom de la tâche");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        nameColumn.setPrefWidth(120);

        TableColumn<TaskTableModel, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        descriptionColumn.setPrefWidth(250);

        TableColumn<TaskTableModel, String> categoryColumn = new TableColumn<>("Catégorie");
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        categoryColumn.setPrefWidth(120);

        TableColumn<TaskTableModel, String> statusColumn = new TableColumn<>("Statut");
        statusColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(translateStatus(cellData.getValue().getStatus())));
        statusColumn.setPrefWidth(75);

        statusColumn.setCellFactory(column -> new TableCell<TaskTableModel, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    TaskTableModel item = getTableRow().getItem();
                    if (item != null) {
                        try {
                            LocalDate dueDate = LocalDate.parse(item.getDueDate());

                            switch (status) {
                                case "Terminée":
                                    setTextFill(Color.GREEN);
                                    setStyle("-fx-font-weight: bold;");
                                    break;
                                case "En cours":
                                    setTextFill(Color.BLUE);
                                    setStyle("-fx-font-weight: bold;");
                                    break;
                                case "À faire":
                                    if (dueDate.isBefore(LocalDate.now())) {
                                        setTextFill(Color.RED);
                                        setStyle("-fx-font-weight: bold;");
                                    } else {
                                        setTextFill(Color.ORANGE);
                                    }
                                    break;
                                default:
                                    setTextFill(Color.BLACK);
                            }
                        } catch (Exception e) {
                            setTextFill(Color.BLACK);
                        }
                    }
                }
            }
        });

        taskTableView.getColumns().addAll(idColumn, nameColumn, descriptionColumn, categoryColumn, statusColumn);
    }

    public void refreshData() {
        taskItems.clear();
        List<Task> tasks = taskRepository.getAllTasks();
        for (Task task : tasks) {
            TaskTableModel item = new TaskTableModel(
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getDueDate().toString(),
                    task.getStatus().toString(),
                    task.getPriority().toString(),
                    task.getCategory().getName());
            taskItems.add(item);
        }
        taskTableView.setItems(taskItems);
        updateCounters();
    }

    private void updateCounters() {

        int[] counters = new int[3]; // [0: completed, 1: inProgress, 2: late]
        LocalDate today = LocalDate.now();

        for (TaskTableModel task : taskItems) {
            String status = task.getStatus();

            if (status.equals(TaskStatus.DONE.toString())) {
                counters[0]++;
            } else if (status.equals(TaskStatus.IN_PROGRESS.toString())) {
                counters[1]++;
            }

            try {
                LocalDate dueDate = LocalDate.parse(task.getDueDate());
                if (dueDate.isBefore(today) && !status.equals(TaskStatus.DONE.toString())) {
                    counters[2]++;
                }
            } catch (Exception e) {
                System.err.println("Erreur de parsing de date pour la tâche: " + task.getId());
            }
        }

        Platform.runLater(() -> {
            completedCountLabel.setText(String.valueOf(counters[0]));
            inProgressCountLabel.setText(String.valueOf(counters[1]));
            lateCountLabel.setText(String.valueOf(counters[2]));

            notificationBadge.setText(String.valueOf(counters[2]));
            notificationBadge.setVisible(counters[2] > 0);
        });
    }

    @FXML
    private void getCreateCategoryTaskView(MouseEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("categoryForm.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.show();
    }

    @FXML
    private void getCreateViewTask(MouseEvent event) throws IOException {
        openTaskForm(null);
    }

    @FXML
    private void getEditViewTask(MouseEvent event) throws IOException {
        TaskTableModel selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            openTaskForm(selectedTask);
        } else {
            helper.alert("Sélection requise", "Veuillez sélectionner une tâche à modifier", "warning");
        }
    }

    private void openTaskForm(TaskTableModel taskToEdit) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("taskForm.fxml"));
        Parent parent = loader.load();

        TaskManagerController controller = loader.getController();
        if (taskToEdit != null) {
            Task task = taskRepository.getTask(taskToEdit.getId());
            controller.loadTaskForEdit(task);
        } else {
            controller.setForNewTask();
        }

        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.initStyle(StageStyle.UTILITY);
        stage.setOnHidden(e -> refreshData());
        stage.show();
    }

    @FXML
    private void getAllTasksCategoriesView(MouseEvent event) throws IOException {
        App.setRoot("home_category");
    }

    @FXML
    private void deletedSelectedTask(MouseEvent event) {
        TaskTableModel selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Etes-vous sûr de vouloir supprimer cette tâche ?");
            alert.setContentText("Tâche : " + selectedTask.getTitle());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Task task = taskRepository.getTask(selectedTask.getId());
                    if (task != null) {
                        taskRepository.deleteTask(task);
                        helper.alert("Suppression réussi",
                                "La tâche " + selectedTask.getTitle() + " a été supprimée avec succès", "success");
                        refreshData();
                    }
                }
            });
        }
    }

    @FXML
    private void getSettings(MouseEvent event) {
        // À implémenter
    }

    @FXML
    private void closeTaskForm(MouseEvent event) {
        helper.closeForm(event);
    }

    @FXML
    private void searchTasks(KeyEvent event) {
        String searchText = search_task_TextField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            taskTableView.setItems(taskItems);
            return;
        }

        ObservableList<TaskTableModel> filteredTasks = FXCollections.observableArrayList();
        for (TaskTableModel task : taskItems) {
            if (task.getTitle().toLowerCase().contains(searchText)) {
                filteredTasks.add(task);
            }
        }
        taskTableView.setItems(filteredTasks);
    }

    @FXML
    private void getCompletedTasks(MouseEvent event) {
        ObservableList<TaskTableModel> filteredTasks = FXCollections.observableArrayList();
        for (TaskTableModel task : taskItems) {
            if (task.getStatus().equals(TaskStatus.DONE.toString())) {
                filteredTasks.add(task);
            }
        }
        taskTableView.setItems(filteredTasks);
    }

    @FXML
    private void getInProcessTasks(MouseEvent event) {
        ObservableList<TaskTableModel> filteredTasks = FXCollections.observableArrayList();
        for (TaskTableModel task : taskItems) {
            if (task.getStatus().equals(TaskStatus.IN_PROGRESS.toString())) {
                filteredTasks.add(task);
            }
        }
        taskTableView.setItems(filteredTasks);
    }

    @FXML
    private void getLateTasks(MouseEvent event) {
        ObservableList<TaskTableModel> filteredTasks = FXCollections.observableArrayList();
        LocalDate today = LocalDate.now();
        for (TaskTableModel task : taskItems) {
            try {
                if (LocalDate.parse(task.getDueDate()).isBefore(today)
                        && !task.getStatus().equals(TaskStatus.DONE.toString())) {
                    filteredTasks.add(task);
                }
            } catch (Exception e) {
                // Gérer l'exception de parsing
            }
        }
        taskTableView.setItems(filteredTasks);
    }

    @FXML
    private void resetFilter(MouseEvent event) {
        taskTableView.setItems(taskItems);
    }

    @FXML
    private void showNotifications(MouseEvent event) {
        List<TaskTableModel> overdueTasks = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (TaskTableModel task : taskItems) {
            try {
                LocalDate dueDate = LocalDate.parse(task.getDueDate());
                if (dueDate.isBefore(today) && !task.getStatus().equals(TaskStatus.DONE.toString())) {
                    overdueTasks.add(task);
                }
            } catch (Exception e) {
                System.err.println("Erreur de parsing de date pour la notification: " + task.getId());
            }
        }

        if (overdueTasks.isEmpty()) {
            helper.alert("Notifications", "Aucune tâche en retard", "info");
        } else {
            StringBuilder sb = new StringBuilder("Tâches en retard :\n\n");
            for (TaskTableModel task : overdueTasks) {
                try {
                    LocalDate dueDate = LocalDate.parse(task.getDueDate());
                    sb.append("• ")
                            .append(task.getTitle())
                            .append(" (Échéance: ")
                            .append(dueDate.format(formatter))
                            .append(")\n");
                } catch (Exception e) {
                    sb.append("• ")
                            .append(task.getTitle())
                            .append(" (Échéance: ")
                            .append(task.getDueDate())
                            .append(")\n");
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Tâches en retard");
            alert.setHeaderText(null);
            alert.setContentText(sb.toString());

            ButtonType markAsRead = new ButtonType("Marquer comme lues");
            ButtonType closeButton = new ButtonType("Fermer");
            alert.getButtonTypes().setAll(markAsRead, closeButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == markAsRead) {
                    notificationBadge.setVisible(false);
                }
            });
        }
    }

    public void stop() {
        if (notificationTimeline != null) {
            notificationTimeline.stop();
        }
    }
}
