/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Helpers;

import Model.TaskCategory;
import Model.TaskUtils;
import java.time.LocalDate;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 * @author jeanc
 */
public class Helpers {
    
     public void alert(String caption, String message, String type) {
        if ("success".equals(type)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(caption);
            alert.setContentText(message);
            alert.showAndWait();
        } else if ("error".equals(type)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(caption);
            alert.setContentText(message);
            alert.showAndWait();
        }

    }
     
     
     public void closeForm(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
     
   
}
