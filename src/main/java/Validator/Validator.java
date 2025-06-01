package Validator;

import Model.TaskUtils.TaskPriority;
import Model.TaskUtils.TaskStatus;
import java.time.LocalDate;
import java.util.Date;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author jeanc
 */
public class Validator {

    public static boolean validateTaskForm(String title, String description, LocalDate dueDate, TaskPriority priority, TaskStatus status) {

        if (title == null || !(title instanceof String) || ((String) title).trim().isEmpty()) {
            return false;
        }
        if (description == null || !(description instanceof String) || ((String) description).trim().isEmpty()) {
            return false;
        }

        if (priority == null) {
            return false;
        }

        if (status == null) {
            return false;
        }

        if (dueDate == null || !(dueDate instanceof LocalDate)) {
            return false;
        }

        return true;
    }

    public static boolean validateCategoryForm(String name) {
        if (name == null || !(name instanceof String) || ((String) name).trim().isEmpty()) {
            return false;
        }
        return true;

    }

}
