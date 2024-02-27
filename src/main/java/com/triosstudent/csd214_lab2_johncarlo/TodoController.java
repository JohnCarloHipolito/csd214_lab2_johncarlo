package com.triosstudent.csd214_lab2_johncarlo;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TodoController {

    @FXML
    private TextField idTF;
    @FXML
    private TextField descriptionTF;
    @FXML
    private DatePicker targetDateDP;
    @FXML
    private ChoiceBox<String> statusCB;

    @FXML
    private Label messageLbl;

    @FXML
    private Button readBtn;
    @FXML
    private Button createBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private Button deleteBtn;

    @FXML
    private TableView<TodoModel> todoTbl;
    @FXML
    private TableColumn<TodoModel, Long> idCol;
    @FXML
    private TableColumn<TodoModel, String> descriptionCol;
    @FXML
    private TableColumn<TodoModel, String> targetDateCol;
    @FXML
    private TableColumn<TodoModel, String> statusCol;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        targetDateCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getTargetDate().format(DateTimeFormatter.ofPattern("M/d/yyyy"))));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // allow selecting todo item from the table
        todoTbl.setRowFactory(tv -> {
            TableRow<TodoModel> row = new TableRow<>(){
                @Override
                protected void updateItem(TodoModel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setStyle("");
                    } else if (item.getStatus().equals("Todo")) {
                        setStyle("-fx-background-color: pink;");
                    } else if (item.getStatus().equals("Doing")) {
                        setStyle("-fx-background-color: lightblue;");
                    } else if (item.getStatus().equals("Done")) {
                        setStyle("-fx-background-color: lightgreen;");
                    } else {
                        setStyle("");
                    }
                }
            };
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    TodoModel selected = row.getItem();
                    idTF.setText(selected.getId().toString());
                    descriptionTF.setText(selected.getDescription());
                    targetDateDP.setValue(selected.getTargetDate());
                    statusCB.setValue(selected.getStatus());
                    messageLbl.setTextFill(Color.GREEN);
                    messageLbl.setText("Todo item selected for update or delete.");
                }
            });
            return row;
        });

        //restrict idTF to accept only integer values
        idTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^([1-9]\\d*)?$")) {
                idTF.setText(oldValue);
            }
        });
    }

    @FXML
    protected void executeQuery(ActionEvent event) {
        String url = "jdbc:mysql://localhost:3306/csd214_lab2_johncarlo";
        String username = "admin";
        String password = "admin";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            if (event.getSource() == readBtn) {
                readTodoItems(connection, true);
            } else if (event.getSource() == createBtn) {
                createTodoItem(connection);
            } else if (event.getSource() == updateBtn) {
                updateTodoItem(connection);
            } else if (event.getSource() == deleteBtn) {
                deleteTodoItem(connection);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void readTodoItems(Connection connection, boolean displayMessage) throws SQLException {
        String readQuery = "SELECT * FROM todo";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(readQuery);
        todoTbl.getItems().clear();
        while (resultSet.next()) {
            Long id = resultSet.getLong("id");
            String description = resultSet.getString("description");
            LocalDate targetDate = resultSet.getDate("target_date").toLocalDate();
            String status = resultSet.getString("status");
            todoTbl.getItems().add(new TodoModel(id, description, targetDate, status));
        }
        if (displayMessage) {
            messageLbl.setTextFill(Color.GREEN);
            messageLbl.setText("Todo items were read successfully.");
        }
        clearInputFields();
    }

    private void createTodoItem(Connection connection) throws SQLException {
        // validate if id is empty
        if (!idTF.getText().isEmpty()) {
            messageLbl.setTextFill(Color.RED);
            messageLbl.setText("'Id' should be empty when adding a todo item.");
            return;
        }
        // validate if description, target date, and status is not empty
        if (descriptionTF.getText().isEmpty() || targetDateDP.getValue() == null || statusCB.getValue() == null) {
            messageLbl.setTextFill(Color.RED);
            messageLbl.setText("'Description', 'Target Date', and 'Status' are required when adding a todo item.");
            return;
        }
        // create todo item
        String createQuery = String.format("INSERT INTO todo (description, target_date, status) VALUES ('%s', '%s', '%s')",
                descriptionTF.getText(), targetDateDP.getValue(), statusCB.getValue());
        Statement createStatement = connection.createStatement();
        createStatement.executeUpdate(createQuery);
        messageLbl.setTextFill(Color.GREEN);
        messageLbl.setText("Todo item was added successfully.");

        readTodoItems(connection, false);
    }

    private void updateTodoItem(Connection connection) throws SQLException {
        // validate if id is not empty
        if (idTF.getText().isEmpty()) {
            messageLbl.setTextFill(Color.RED);
            messageLbl.setText("'Id' is required when updating a todo item.");
            return;
        }
        // validate if id exists in the table.
        Long providedId = Long.parseLong(idTF.getText());
        boolean idExists = todoTbl.getItems().stream().anyMatch(todo -> todo.getId().equals(providedId));
        if (!idExists) {
            messageLbl.setTextFill(Color.RED);
            messageLbl.setText("An existing 'Id' is required when updating a todo item.");
            return;
        }
        // validate if description, target date, and status is not empty
        if (descriptionTF.getText().isEmpty() || targetDateDP.getValue() == null || statusCB.getValue() == null) {
            messageLbl.setTextFill(Color.RED);
            messageLbl.setText("'Description', 'Target Date', and 'Status' are required when updating a todo item.");
            return;
        }
        // update todo item
        String updateQuery = String.format("UPDATE todo SET description = '%s', target_date = '%s', status = '%s' WHERE id = %s",
                descriptionTF.getText(), targetDateDP.getValue(), statusCB.getValue(), idTF.getText());
        Statement updateStatement = connection.createStatement();
        updateStatement.executeUpdate(updateQuery);
        messageLbl.setTextFill(Color.GREEN);
        messageLbl.setText("Todo item was updated successfully.");

        readTodoItems(connection, false);
    }

    private void deleteTodoItem(Connection connection) throws SQLException {
        // validate if id is not empty
        if (idTF.getText().isEmpty()) {
            messageLbl.setTextFill(Color.RED);
            messageLbl.setText("'Id' is required when deleting a todo item.");
            return;
        }
        // validate if id exists in the table
        Long providedId = Long.parseLong(idTF.getText());
        boolean idExists = todoTbl.getItems().stream().anyMatch(todo -> todo.getId().equals(providedId));
        if (!idExists) {
            messageLbl.setTextFill(Color.RED);
            messageLbl.setText("An existing 'Id' is required when deleting a todo item.");
            return;
        }
        // delete todo item
        String deleteQuery = String.format("DELETE FROM todo WHERE id = %s", idTF.getText());
        Statement deleteStatement = connection.createStatement();
        deleteStatement.executeUpdate(deleteQuery);
        messageLbl.setTextFill(Color.GREEN);
        messageLbl.setText("Todo item was deleted successfully.");

        readTodoItems(connection, false);
    }

    private void clearInputFields() {
        idTF.clear();
        descriptionTF.clear();
        targetDateDP.setValue(null);
        statusCB.setValue(null);
    }
}