module jeancygrys.taskmanagerapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
        
    opens jeancygrys.taskmanagerapp to javafx.fxml;
    opens Model to javafx.base;
    exports jeancygrys.taskmanagerapp;
    requires fontawesomefx;
   

 
}
