/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.TaskUtils.TaskPriority;
import Model.TaskUtils.TaskStatus;
import java.time.LocalDate;

/**
 *
 * @author jeanc
 */
public class Task {

    private String title;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
    private TaskPriority priority;
    private TaskCategory category;
    private int id;

    public Task(int id, String title, String description, LocalDate dueDate, TaskStatus status, TaskPriority priority, TaskCategory category) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the dueDate
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * @param dueDate the dueDate to set
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }



    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(TaskCategory category) {
        this.category = category;
    }

    /**
     * @return the status
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * @return the priority
     */
    public TaskPriority getPriority() {
        return priority;
    }

    /**
     * @return the category
     */
    public TaskCategory getCategory() {
        return category;
    }

}
