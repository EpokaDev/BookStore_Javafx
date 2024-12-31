package application.bookstore.views;

import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.controllers.UsersTableController;
import application.bookstore.models.User;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;


public class UsersTableView extends VBox implements DatabaseConnector {

    private final TableView<User> tableView;
    private TableColumn<User, String> firstNameColumn;
    private TableColumn<User, String> lastNameColumn;
    private TableColumn<User, String> emailColumn;
    private TableColumn<User,String> userNameColumn;
    private TableColumn<User, String> passwordColumn;
    private TableColumn<User, String> genderColumn;
    private TableColumn<User, String> roleColumn;

    private Button addButton;
    private Button removeButton;
    private final ObservableList<User> users;

    public UsersTableView(ObservableList<User> currentUsers) {
        this.users=currentUsers;
        tableView = new TableView<>();
        tableView.setItems(users);
        displayTable();
    }

    private void displayTable() {

        tableView.setEditable(true);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);//to remove empty columns
        tableView.setStyle("-fx-font-size: 16;");
        tableView.setPrefHeight(300);
        tableView.setEditable(true);

        setSpacing(20);
        setPadding(new Insets(40,30,30,30));

        firstNameColumn = new TableColumn<User, String>("First Name");
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());


        lastNameColumn = new TableColumn<User, String>("Last Name");
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());


        emailColumn = new TableColumn<User, String>("Email");
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());


        userNameColumn = new TableColumn<User, String>("Username");
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        userNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        passwordColumn = new TableColumn<User, String>("Password");
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());
        passwordColumn.setCellFactory(TextFieldTableCell.forTableColumn());


        genderColumn = new TableColumn<User, String>("Gender");
        genderColumn.setCellValueFactory(cellData -> cellData.getValue().genderProperty());
        genderColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        roleColumn = new TableColumn<User, String>("Role");
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().getRole());
        roleColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        tableView.getColumns().addAll(firstNameColumn,lastNameColumn,emailColumn,userNameColumn,
                passwordColumn,genderColumn,roleColumn);

        HBox hBox=new HBox();
        hBox.setSpacing(20);
        hBox.setPadding(new Insets(10,10,10,10));
        hBox.setAlignment(Pos.CENTER);

        addButton=new Button("Add");
        addButton.setPrefWidth(100);
        addButton.setFont(Font.font(20));

        removeButton=new Button("Remove");
        removeButton.setPrefWidth(100);
        removeButton.setFont(Font.font(20));
        hBox.getChildren().addAll(addButton,removeButton);

        getChildren().addAll(tableView,hBox);

        new UsersTableController(this,users);
    }


    public TableColumn<User, String> getFirstNameColumn()
    {
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

