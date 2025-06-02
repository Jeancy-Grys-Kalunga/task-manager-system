/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Repository;

import Model.Task;
import Helpers.Helpers;
import Model.TaskCategory;
import Validator.Validator;
import database.Db;
import java.sql.Connection;
import java.sql.Date;
//import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeancygrys.taskmanagerapp.TaskManagerController;

/**
 *
 * @author jeanc
 */
public class TaskCategoryRepository {

    private Db db = new Db();
    private Connection connection;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Helpers helper = new Helpers();

    public TaskCategoryRepository() {
        connection = db.getConnection();

    }

    public int addCategory(TaskCategory taskCategory) {
        if (Validator.validateCategoryForm(taskCategory.getName())) {
            String query = "INSERT INTO categories(name) "
                    + "VALUES (?)";
            try (
                    PreparedStatement categoryStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                categoryStatement.setString(1, taskCategory.getName());
                categoryStatement.executeUpdate();

                try (ResultSet generatedKeys = categoryStatement.getGeneratedKeys()) {

                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        logger.info("Erreur servenue lors de la génération de l'Id");
                    }
                }

            } catch (SQLException e) {
                helper.alert("Enregistrement échoué", " " + e, "error");

            }
        }
        return -1;
    }

    public void updateCategory(TaskCategory taskCategory) {
        if (Validator.validateCategoryForm(taskCategory.getName())) {
            String query = "UPDATE categories SET name = ? WHERE id = ? ";

            try (
                    PreparedStatement categoryStatement = connection.prepareStatement(query)) {
                categoryStatement.setString(1, taskCategory.getName());
                categoryStatement.setInt(2, taskCategory.getId());
                categoryStatement.executeUpdate();

            } catch (SQLException e) {
                logger.info(e.toString());
            }
        }
    }

    public void deleteCategory(TaskCategory taskCategory) {

        String query = "DELETE FROM categories WHERE id = ? ";

        try (
                PreparedStatement categoryStatement = connection.prepareStatement(query)) {
            categoryStatement.setInt(1, taskCategory.getId());
            categoryStatement.executeUpdate();

        } catch (SQLException e) {
            logger.info(e.toString());
        }
    }

    public List<TaskCategory> getAllCategories() {
        String query = "SELECT * FROM categories";
        List<TaskCategory> categories = new ArrayList<>();

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                TaskCategory category = new TaskCategory(id, name);
                categories.add(category);
            }
        } catch (SQLException e) {
            logger.info(e.toString());
        }

        return categories;
    }

    public TaskCategory getCategoryById(int id) {
        String query = "SELECT * FROM categories WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TaskCategory(
                            rs.getInt("id"),
                            rs.getString("name")
                    );
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération de la catégorie", e);
        }
        return null;
    }

}
