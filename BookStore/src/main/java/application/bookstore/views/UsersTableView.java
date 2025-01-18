package application.bookstore.views;

import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.controllers.UsersTableController;
import application.bookstore.models.User;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class UsersTableView extends VBox implements DatabaseConnector {

    private final TableView<User> tableView;
    private TableColumn<User, String> firstNameColumn;
    private TableColumn<User, String> lastNameColumn;
    private TableColumn<User, String> emailColumn;
    private TableColumn<User, String> userNameColumn;
    private TableColumn<User, String> passwordColumn;
    private TableColumn<User, String> genderColumn;
    private TableColumn<User, String> roleColumn;

    private Button addButton;
    private Button removeButton;
    private final ObservableList<User> users;

    public UsersTableView(ObservableList<User> currentUsers) {
        this.users = currentUsers;
        // Create our own TableView
        this.tableView = new TableView<>();
        tableView.setItems(users);

        // Then build everything
        displayTable();
    }

    public UsersTableView(
            TableView<User> tableView,
            TableColumn<User, String> firstNameColumn,
            TableColumn<User, String> lastNameColumn,
            TableColumn<User, String> emailColumn,
            TableColumn<User, String> userNameColumn,
            TableColumn<User, String> passwordColumn,
            TableColumn<User, String> genderColumn,
            TableColumn<User, String> roleColumn,
            Button addButton,
            Button removeButton,
            ObservableList<User> currentUsers
    ) {
        this.tableView = tableView;
        this.firstNameColumn = firstNameColumn;
        this.lastNameColumn = lastNameColumn;
        this.emailColumn = emailColumn;
        this.userNameColumn = userNameColumn;
        this.passwordColumn = passwordColumn;
        this.genderColumn = genderColumn;
        this.roleColumn = roleColumn;
        this.addButton = addButton;
        this.removeButton = removeButton;
        this.users = currentUsers;

        displayTable();
    }

    private void displayTable() {
        tableView.setEditable(true);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.setStyle("-fx-font-size: 16;");
        tableView.setPrefHeight(300);
        tableView.setEditable(true);
        tableView.setItems(users);

        setSpacing(20);
        setPadding(new Insets(40,30,30,30));

        // Ensure columns use TextFieldTableCell factories
        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        genderColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        roleColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // If columns are not yet added, ensure they're in the table
        if (!tableView.getColumns().contains(firstNameColumn)) {
            tableView.getColumns().addAll(
                    firstNameColumn, lastNameColumn, emailColumn, userNameColumn,
                    passwordColumn, genderColumn, roleColumn
            );
        }

        HBox hBox = new HBox();
        hBox.setSpacing(20);
        hBox.setPadding(new Insets(10,10,10,10));
        hBox.setAlignment(Pos.CENTER);

        addButton.setPrefWidth(100);
        addButton.setFont(Font.font(20));

        removeButton.setPrefWidth(100);
        removeButton.setFont(Font.font(20));

        hBox.getChildren().addAll(addButton, removeButton);

        getChildren().addAll(tableView, hBox);

        // Create controller to wire up events
        new UsersTableController(this, users);
    }

    // Getters
    public TableColumn<User, String> getFirstNameColumn() {
        return firstNameColumn;
    }

    public TableView<User> getTableView() {
        return tableView;
    }

    public TableColumn<User, String> getLastNameColumn() {
        return lastNameColumn;
    }

    public TableColumn<User, String> getEmailColumn() {
        return emailColumn;
    }

    public TableColumn<User, String> getUserNameColumn() {
        return userNameColumn;
    }

    public TableColumn<User, String> getPasswordColumn() {
        return passwordColumn;
    }

    public TableColumn<User, String> getGenderColumn() {
        return genderColumn;
    }

    public TableColumn<User, String> getRoleColumn() {
        return roleColumn;
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getRemoveButton() {
        return removeButton;
    }
}
