/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 *
 * @author jeanc
 */
public class Db {

    private Connection connection;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:sqlite:tasks.db");
                logger.info("Connected to the database");
                this.createTableIfNotExists();
                return connection;
            }
        } catch (SQLException e) {
            logger.info(e.toString());
        }
        return connection;
    }

    private void createTableIfNotExists() {
        this.getConnection();

        String sqlCategories = "CREATE TABLE IF NOT EXISTS categories("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL UNIQUE"
                + ")";

        String sqlTasks = "CREATE TABLE IF NOT EXISTS tasks ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT NOT NULL,"
                + "description TEXT NULL,"
                + "dueDate DATE NOT NULL," 
                + "priority TEXT NULL,"
                + "category_id INTEGER NOT NULL,"
                + "status TEXT NOT NULL,"
                + " FOREIGN KEY (category_id) REFERENCES categories(id)"
                + ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sqlCategories);
            statement.execute(sqlTasks);
            logger.info("Tables created or tables existing....");

        } catch (SQLException e) {
            logger.info(e.toString());
        }

    }
}
