/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package jeancygrys.taskmanagerapp;

import Helpers.Helpers;
import Model.Task;
import Model.TaskCategory;
import Model.TaskUtils.TaskPriority;
import Model.TaskUtils.TaskStatus;
import Repository.TaskCategoryRepository;
import Repository.TaskRepository;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author jeanc
 */
public class HomeController implements Initializable {

    @FXML
    private Button getAllTasksCategoriesView;
    @FXML
    private Button getSettingsButton;
    @FXML
    private ImageView getCompletedTasksButton;
    @FXML
    private Button inProcessButton;
    @FXML
    private Button getViewAddTaskButton;
    @FXML
    private ImageView btnAddTask;
    @FXML
    private Button getViewAddCategoryTaskButton;
    @FXML
    private TextField search_task_TextField;
    private TableView<Task> TasksTableView;

    private TaskManagerController taskController;
    private TaskRepository taskRepository = new TaskRepository();
    private TaskCategoryRepository taskCategoryRepository = new TaskCategoryRepository();
    @FXML
    private Button InLatestBtn;
    @FXML
    private Button getViewAddTaskButton111;
    @FXML
    private ImageView btnAddTask111;
    @FXML
    private Button getViewAddTaskButton1112;
    @FXML
    private ImageView btnAddTask1112;
    @FXML
    private Button getViewAddTaskButton11121;
    @FXML
    private ImageView btnAddTask11121;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    @FXML
    private TableView<?> taskTableView;

    private Helpers helper = new Helpers();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeTasksTable();
        refreshTasksTable();
    }

    @FXML
    private void getAllTasksCategoriesView(MouseEvent event) throws IOException {
        App.setRoot("home_category");
    }

    private void initializeTasksTable() {
        TableColumn<Task, String> titleColumn = new TableColumn<>("Titre");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Task, TaskCategory> categoryColumn = new TableColumn<>("Catégorie");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(TaskCategory item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        TableColumn<Task, TaskPriority> priorityColumn = new TableColumn<>("Priorité");
        priorityColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(TaskPriority item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(taskController.getPriorityFromTranslation(item));
                }
            }
        });

        TableColumn<Task, TaskStatus> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(TaskStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(taskController.getStatusFromTranslation(item));
                }
            }
        });

        TableColumn<Task, LocalDate> due_DateColumn = new TableColumn<>("Délai (Jours)");
        due_DateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        due_DateColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });

        TasksTableView.getColumns().addAll(titleColumn, descriptionColumn, categoryColumn, statusColumn, priorityColumn, due_DateColumn);
        TasksTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldTask, newTask) -> {
//            if(newTask != null){
//                showTaskDetails(newTask);
//            }
        });

        refreshTasksTable();

    }

//    
    private void refreshTasksTable() {

        try {
            List<Task> tasks = taskRepository.getAllTasks();
            ObservableList<Task> observableCategories = FXCollections.observableArrayList(tasks);
            TasksTableView.setItems(observableCategories);

        } catch (Exception ex) {
            logger.info(ex.toString());
        }

    }

    @FXML
    private void getSettings(MouseEvent event) {
    }

    @FXML
    private void addTask(MouseEvent event) {
    }

    @FXML
    public void getCreateViewTask(MouseEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("addTask.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.show();
    }

    @FXML
    public void getCreateCategoryTaskView(MouseEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("addCategory.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.show();
    }

   

}
