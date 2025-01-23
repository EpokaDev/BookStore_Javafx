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

    private UsersTableController controller;

    private boolean not_testing=true;
    private Button addButton;
    private Button removeButton;
    private final ObservableList<User> users;


    public UsersTableView(ObservableList<User> currentUsers) {
        this.users = currentUsers;
        // Create our own TableView
        this.tableView = new TableView<>();
        tableView.setItems(users);
        initializeColumns(); // Initialize columns
        this.addButton = new Button("Add User");
        this.removeButton = new Button("Remove User");
        this.not_testing=true;
        // Then build everything
        displayTable();
    }

    public UsersTableView(ObservableList<User> currentUsers, boolean testing ) {
        this.not_testing=false;
        this.users = currentUsers;
        this.tableView = new TableView<>();
        tableView.setItems(users);
        initializeColumns();
        this.addButton = new Button("Add User");
        this.removeButton = new Button("Remove User");

        // Set fx:id for buttons
        this.addButton.setId("addButton");
        this.removeButton.setId("removeButton");

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
        initializeColumns(); // Initialize columns
        this.addButton = new Button("Add User");
        this.addButton.setId("addButton"); // Set fx:id
        this.removeButton = new Button("Remove User");
        this.removeButton.setId("removeButton"); // Set fx:id

        displayTable();
    }

    private void initializeColumns() {
        firstNameColumn = new TableColumn<>("First Name");
        lastNameColumn = new TableColumn<>("Last Name");
        emailColumn = new TableColumn<>("Email");
        userNameColumn = new TableColumn<>("Username");
        passwordColumn = new TableColumn<>("Password");
        genderColumn = new TableColumn<>("Gender");
        roleColumn = new TableColumn<>("Role");

        // Set column value factories (assuming User class has the respective getters)
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());
        genderColumn.setCellValueFactory(cellData -> cellData.getValue().genderProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().getRole());
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

        if(not_testing)
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


    // Setter for the controller
    public void setController(UsersTableController controller) {
        this.controller = controller;

        displayTable();
    }


}
