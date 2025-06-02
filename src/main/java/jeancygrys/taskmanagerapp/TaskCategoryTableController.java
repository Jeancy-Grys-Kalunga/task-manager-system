package jeancygrys.taskmanagerapp;

import Helpers.Helpers;
import Model.Task;
import Model.TaskCategory;
import Model.TaskUtils.TaskStatus;
import Repository.TaskCategoryRepository;
import Repository.TaskRepository;
import TableController.TaskCategoryTableModel;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class TaskCategoryTableController {

    @FXML
    public TableView<TaskCategoryTableModel> categoryTableView;
    public ObservableList<TaskCategoryTableModel> categoriesItems;
    private TaskCategoryRepository taskCategoryRepository = new TaskCategoryRepository();
    private TaskRepository taskRepository = new TaskRepository();
    private Helpers helper = new Helpers();
    private Timeline notificationTimeline;

    @FXML
    private Button getAllTaskButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button completedTaskButton;
    @FXML
    private Button inProcessTaskButton;
    @FXML
    private Button getCreateTaskViewsButton;
    @FXML
    private Button getCreateTaskCategoryView;
    @FXML
    private Button InLatestBtn;
    @FXML
    private TextField search_taskCategory_TextField;
    @FXML
    private Button deleteCategoryBtn;
    @FXML
    private Button getViewAddCategoryButton;
    @FXML
    private Button getEditCategoryView;
    @FXML
    private Button resetFilterButton;
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
        updateCounters();
        startNotificationChecker();
    }

    private void startNotificationChecker() {
        notificationTimeline = new Timeline(
                new KeyFrame(Duration.minutes(1), event -> {
                    updateCounters();
                    checkForNewNotifications();
                })
        );
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
                helper.alert("Nouvelles tâches en retard", "Nouvelles tâches en retard détectées!", "success");
            });
        }
    }

    private int calculateLateCount() {
        int lateCount = 0;
        LocalDate today = LocalDate.now();
        List<Task> tasks = taskRepository.getAllTasks();

        for (Task task : tasks) {
            if (task.getDueDate().isBefore(today) && task.getStatus() != TaskStatus.DONE) {
                lateCount++;
            }
        }
        return lateCount;
    }

    private void initializeColumns() {
        categoryTableView.getColumns().clear();

        TableColumn<TaskCategoryTableModel, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(cellData
                -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        idColumn.setPrefWidth(60);

        TableColumn<TaskCategoryTableModel, String> nameColumn = new TableColumn<>("Nom de la catégorie");
        nameColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setPrefWidth(250);

        categoryTableView.getColumns().addAll(idColumn, nameColumn);
    }

    public void refreshData() {
        categoriesItems = FXCollections.observableArrayList();
        List<TaskCategory> categories = taskCategoryRepository.getAllCategories();
        for (TaskCategory category : categories) {
            TaskCategoryTableModel item = new TaskCategoryTableModel(
                    category.getId(),
                    category.getName()
            );
            categoriesItems.add(item);
        }
        categoryTableView.setItems(categoriesItems);
    }

    private void updateCounters() {
        List<Task> tasks = taskRepository.getAllTasks();
        int completed = 0, inProgress = 0, late = 0;
        LocalDate today = LocalDate.now();

        for (Task task : tasks) {
            if (task.getStatus() == TaskStatus.DONE) {
                completed++;
            } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                inProgress++;
            }

            if (task.getDueDate().isBefore(today) && task.getStatus() != TaskStatus.DONE) {
                late++;
            }
        }

        completedCountLabel.setText(String.valueOf(completed));
        inProgressCountLabel.setText(String.valueOf(inProgress));
        lateCountLabel.setText(String.valueOf(late));

        notificationBadge.setText(String.valueOf(late));
        notificationBadge.setVisible(late > 0);
    }

    @FXML
    private void getAllTaskView(MouseEvent event) throws IOException {
        App.setRoot("home");
    }

    @FXML
    private void getSettingView(MouseEvent event) {
        // À implémenter
    }

    @FXML
    private void addTask(MouseEvent event) {
    }

    @FXML
    private void getCompletedTasks(MouseEvent event) throws IOException {
        App.currentTaskFilter = "COMPLETED";
        App.setRoot("home");
    }

    @FXML
    private void getInProcessTasks(MouseEvent event) throws IOException {
        App.currentTaskFilter = "IN_PROGRESS";
        App.setRoot("home");
    }

    @FXML
    private void getCreateTask(MouseEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("taskForm.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.show();
    }

    @FXML
    private void getCreateCategoryTask(MouseEvent event) throws IOException {
        openCategoryForm(null);
    }

    @FXML
    private void getEditCategory(MouseEvent event) throws IOException {
        TaskCategoryTableModel selectedCategory = categoryTableView.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            openCategoryForm(selectedCategory);
        } else {
            helper.alert("Sélection requise", "Veuillez sélectionner une catégorie à modifier", "warning");
        }
    }

    private void openCategoryForm(TaskCategoryTableModel categoryToEdit) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("categoryForm.fxml"));
        Parent parent = loader.load();

        CategoryFormController controller = loader.getController();
        if (categoryToEdit != null) {
            TaskCategory category = taskCategoryRepository.getCategoryById(categoryToEdit.getId());
            controller.loadCategoryForEdit(category);
        } else {
            controller.setForNewCategory();
        }

        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.initStyle(StageStyle.UTILITY);
        stage.setOnHidden(e -> refreshData());
        stage.show();
    }

    @FXML
    private void deleteSelectedCategory(MouseEvent event) {
        TaskCategoryTableModel selectedCategory = categoryTableView.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette catégorie ?");
            alert.setContentText("Catégorie : " + selectedCategory.getName());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    TaskCategory category = new TaskCategory(selectedCategory.getId(), selectedCategory.getName());
                    taskCategoryRepository.deleteCategory(category);
                    helper.alert("Suppression réussie", "La catégorie " + selectedCategory.getName() + " a été supprimée", "success");
                    refreshData();
                }
            });
        }
    }

    @FXML
    private void resetFilter(MouseEvent event) {
        search_taskCategory_TextField.clear();
        refreshData();
    }

    @FXML
    private void searchCategories(KeyEvent event) {
        String searchText = search_taskCategory_TextField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            categoryTableView.setItems(categoriesItems);
            return;
        }

        ObservableList<TaskCategoryTableModel> filteredCategories = FXCollections.observableArrayList();
        for (TaskCategoryTableModel category : categoriesItems) {
            if (category.getName().toLowerCase().contains(searchText)) {
                filteredCategories.add(category);
            }
        }
        categoryTableView.setItems(filteredCategories);
    }

    @FXML
    private void getLateTasks(MouseEvent event) throws IOException {
        App.currentTaskFilter = "LATE";
        App.setRoot("home");
    }

    @FXML
    private void showNotifications(MouseEvent event) {
        List<Task> overdueTasks = taskRepository.getOverdueTasks();
        if (overdueTasks.isEmpty()) {
            helper.alert("Notifications", "Aucune tâche en retard", "info");
        } else {
            StringBuilder sb = new StringBuilder("Tâches en retard :\n\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Task task : overdueTasks) {
                sb.append("• ")
                        .append(task.getTitle())
                        .append(" (Échéance: ")
                        .append(task.getDueDate().format(formatter))
                        .append(")\n");
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
