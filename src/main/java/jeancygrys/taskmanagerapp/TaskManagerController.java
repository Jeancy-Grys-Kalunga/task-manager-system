/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jeancygrys.taskmanagerapp;

import Helpers.Helpers;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import Model.Task;
import Model.TaskCategory;
import Model.TaskUtils;
import Model.TaskUtils.TaskPriority;
import Model.TaskUtils.TaskStatus;
import Repository.TaskCategoryRepository;
import Repository.TaskRepository;
import Validator.Validator;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 * @author jeanc
 */
public class TaskManagerController implements Initializable {

    @FXML
    private DatePicker dateDueComboBox;
    @FXML
    private TextField titleTextField;
    @FXML
    private ComboBox<TaskPriority> priorityComboBox;
    private ComboBox<?> categoryListComboBox;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private Label formTitleLabel;
    @FXML
    private Label taskTUpdated;

    private TaskRepository taskRepository = new TaskRepository();
    private TaskCategoryRepository taskCategoryRepository = new TaskCategoryRepository();
    private TaskTableController taskTableController = new TaskTableController();
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Task editingTask; // Nouvel attribut pour stocker la tâche en cours d'édition

    @FXML
    private ComboBox<TaskStatus> statusComboxBox;
    private Validator validator = new Validator();

    private Helpers helper = new Helpers();

    private TextField categoryName;
    @FXML
    private Button closeTaskFormButton;
    @FXML
    private Button saveTaskButton;
    @FXML
    private ComboBox<TaskCategory> categoryComboBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        editingTask = null; // Initialiser à null
        initializedFormControl();
    }

    public void setForNewTask() {
        resetTaskForm();
        formTitleLabel.setText("Création de tâche");
        taskTUpdated.setText("Nouvelle tâche");
    }

    public void initializedFormControl() {
        statusComboxBox.getItems().addAll(TaskStatus.values());
        priorityComboBox.getItems().addAll(TaskPriority.values());

        List<TaskCategory> categories = taskCategoryRepository.getAllCategories();
        categoryComboBox.getItems().addAll(categories);
        categoryComboBox.setCellFactory(param -> new ListCell<>() {
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

        categoryComboBox.setButtonCell(new ListCell<>() {
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

        priorityComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TaskPriority item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getPriorityFromTranslation(item));
                }
            }
        });

        priorityComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(TaskPriority item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getPriorityFromTranslation(item));
                }
            }
        });

        statusComboxBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TaskStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getStatusFromTranslation(item));
                }
            }
        });
        statusComboxBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(TaskStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getStatusFromTranslation(item));
                }
            }
        });

    }

    // Nouvelle méthode pour charger une tâche dans le formulaire d'édition
    public void loadTaskForEdit(Task task) {
        this.editingTask = task;
        formTitleLabel.setText("Édition de tâche");
        taskTUpdated.setText(task.getTitle());
        titleTextField.setText(task.getTitle());
        descriptionTextArea.setText(task.getDescription());
        dateDueComboBox.setValue(task.getDueDate());
        categoryComboBox.getSelectionModel().select(task.getCategory());
        priorityComboBox.getSelectionModel().select(task.getPriority());
        statusComboxBox.getSelectionModel().select(task.getStatus());
    }

    public String getPriorityFromTranslation(TaskPriority priority) {
        switch (priority) {
            case LOW:
                return "Faible";
            case MEDUIM:
                return "Moyenne";
            case HIGH:
                return "Elevée";
            default:
                throw new IllegalArgumentException("Priorité invalide : " + priority);

        }
    }

    public String getStatusFromTranslation(TaskStatus status) {
        switch (status) {
            case TODO:
                return "A faire";
            case IN_PROGRESS:
                return "En cours";
            case DONE:
                return "Terminée";
            default:
                throw new IllegalArgumentException("Status invalide : " + status);

        }
    }

    public TaskPriority getSelectedPriority() {
        return priorityComboBox.getSelectionModel().getSelectedItem();
    }

    public TaskStatus getSelectedStatus() {

        return statusComboxBox.getSelectionModel().getSelectedItem();
    }

    private void resetTaskForm() {
        formTitleLabel.setText("Création de tâche");
        taskTUpdated.setText("Nouvelle tâche");
        titleTextField.setText(null);
        descriptionTextArea.setText(null);
        dateDueComboBox.setValue(null);
        categoryComboBox.getSelectionModel().clearSelection();
        priorityComboBox.getSelectionModel().clearSelection();
        statusComboxBox.getSelectionModel().clearSelection();
        editingTask = null; // Réinitialiser après modification
    }

    @FXML
    private void closeTaskForm(MouseEvent event) {
        helper.closeForm(event);
    }

    @FXML
    private void storeTask(MouseEvent event) {
        String title = titleTextField.getText();
        String description = descriptionTextArea.getText();
        LocalDate dateDue = dateDueComboBox.getValue();
        TaskCategory category_task = categoryComboBox.getSelectionModel().getSelectedItem();
        TaskPriority priority = priorityComboBox.getSelectionModel().getSelectedItem();
        TaskStatus status = statusComboxBox.getSelectionModel().getSelectedItem();

        if (Validator.validateTaskForm(title, description, dateDue, priority, status)) {
            try {
                if (editingTask != null) {
                    editingTask.setTitle(title);
                    editingTask.setDescription(description);
                    editingTask.setDueDate(dateDue);
                    editingTask.setCategory(category_task);
                    editingTask.setPriority(priority);
                    editingTask.setStatus(status);

                    taskRepository.updateTask(editingTask);
                    helper.alert("Mise à jour réussie", "Tâche mise à jour avec succès", "success");
                } else {
                    Task task = new Task(0, title, description, dateDue, status, priority, category_task);
                    taskRepository.addTask(task);
                    helper.alert("Enregistrement réussi", "La tâche " + title + " est enregistrée avec succès",
                            "success");
                }

                // Fermer le formulaire après enregistrement
                Stage stage = (Stage) saveTaskButton.getScene().getWindow();
                stage.close();

            } catch (Exception ex) {
                logger.info(ex.toString());
            }
        } else {
            helper.alert("Enregistrement échoué", "Veuillez renseigner tous les champs obligatoires", "error");
        }
    }

    private void closeCategoryView(MouseEvent event) {
        helper.closeForm(event);
    }

    private void storeCategory(MouseEvent event) {

        String name = categoryName.getText();

        if (Validator.validateCategoryForm(name)) {
            TaskCategory category = new TaskCategory(0, name);
            taskCategoryRepository.addCategory(category);

            helper.alert("Enregistrement réussi", "La liste  " + name + " est enregistrée avec succès", "success");

        } else {
            helper.alert("Enregistrement échoué", "veuillez renseigner toutes les champs", "error");
        }

    }

}