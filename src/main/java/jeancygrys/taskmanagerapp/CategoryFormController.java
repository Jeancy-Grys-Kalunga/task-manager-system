package jeancygrys.taskmanagerapp;

import Helpers.Helpers;
import Model.TaskCategory;
import Repository.TaskCategoryRepository;
import Validator.Validator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class CategoryFormController {

    @FXML
    private TextField nameField;
    @FXML
    private Label formTitle;

    private TaskCategoryRepository repository = new TaskCategoryRepository();
    private Helpers helper = new Helpers();
    private TaskCategory editingCategory;

    public void setForNewCategory() {
        formTitle.setText("Nouvelle catégorie");
        editingCategory = null;
    }

    public void loadCategoryForEdit(TaskCategory category) {
        this.editingCategory = category;
        formTitle.setText("Modification de catégorie");
        nameField.setText(category.getName());
    }

    @FXML
    private void saveCategory(MouseEvent event) {
        String name = nameField.getText().trim();

        if (!Validator.validateCategoryForm(name)) {
            helper.alert("Erreur de validation", "Le nom de la catégorie est requis", "error");
            return;
        }

        if (editingCategory == null) {
            // Création
            TaskCategory category = new TaskCategory(0, name);
            repository.addCategory(category);
            helper.alert("Succès", "Catégorie créée avec succès", "success");
        } else {
            // Modification
            editingCategory.setName(name);
            repository.updateCategory(editingCategory);
            helper.alert("Succès", "Catégorie modifiée avec succès", "success");
        }

        closeForm(event);
    }

    @FXML
    private void closeForm(MouseEvent event) {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}