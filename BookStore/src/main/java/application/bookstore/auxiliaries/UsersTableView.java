package application.bookstore.auxiliaries;

import application.bookstore.models.User;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;

public class UsersTableView extends VBox implements DatabaseConnector{

    private TableView<User> tableView;
    private TableColumn<User, String> firstNameColumn;
    private TableColumn<User, String> lastNameColumn;
    private TableColumn<User, String> emailColumn;
    private TableColumn<User,String> userNameColumn;
    private TableColumn<User, String> passwordColumn;
    private TableColumn<User, String> genderColumn;
    private TableColumn<User, String> roleColumn;

    private Button addButton;
    private Button removeButton;
    private ObservableList<User> users;

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
        firstNameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> event) {
                User user = event.getRowValue();
                user.setFirstName(event.getNewValue());
                updateRowInDatabase(user,"firstName",user.getFirstName(),"userName", user.getUsername());
            }
        });

        lastNameColumn = new TableColumn<User, String>("Last Name");
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> event) {
                User user = event.getRowValue();
                user.setLastName(event.getNewValue());
                updateRowInDatabase(user,"lastName",user.getLastName(),"userName", user.getUsername());
            }
        });

        emailColumn = new TableColumn<User, String>("Email");
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> event) {
                User user = event.getRowValue();
                user.setEmail(event.getNewValue());
                updateRowInDatabase(user,"email",user.getEmail(),"userName", user.getUsername());

            }
        });

        userNameColumn = new TableColumn<User, String>("Username");
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        userNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userNameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> event) {
                User user = event.getRowValue();
                user.setUsername(event.getNewValue());
                updateRowInDatabase(user,"userName",user.getUsername(),"password", user.getPassword());
                //only here we condtion with the password since we are changing the username
            }
        });

        passwordColumn = new TableColumn<User, String>("Password");
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());
        passwordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> event) {
                User user = event.getRowValue();
                user.setPassword(event.getNewValue());
                updateRowInDatabase(user,"password",user.getPassword(),"userName", user.getUsername());
            }
        });

        genderColumn = new TableColumn<User, String>("Gender");
        genderColumn.setCellValueFactory(cellData -> cellData.getValue().genderProperty());
        genderColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        genderColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> event) {
                User user = event.getRowValue();
                user.setGender(event.getNewValue());
                updateRowInDatabase(user,"gender",user.getGender(),"userName", user.getUsername());
            }
        });

        roleColumn = new TableColumn<User, String>("Role");
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().getRole());
        roleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        roleColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> event) {
                User user = event.getRowValue();
                user.setRole(event.getNewValue());
                updateRowInDatabase(user,"Role",user.getRoleString(),"userName",user.getUsername());
            }
        });

        tableView.getColumns().addAll(firstNameColumn,lastNameColumn,emailColumn,userNameColumn,
                passwordColumn,genderColumn,roleColumn);

        HBox hBox=new HBox();
        hBox.setSpacing(20);
        hBox.setPadding(new Insets(10,10,10,10));
        hBox.setAlignment(Pos.CENTER);

        addButton=new Button("Add");
        addButton.setPrefWidth(100);
        addButton.setFont(Font.font(20));
        addButton.setOnAction(e->{
            Stage addUserStage = new Stage();
            addUserStage.setTitle("Add User");

            AddNewUserPane addNewUserPane=new AddNewUserPane(users,addUserStage);
            addNewUserPane.setAlignment(Pos.CENTER);

            addUserStage.setScene(new Scene(addNewUserPane,700,650));
            addUserStage.show();
        });

        removeButton=new Button("Remove");
        removeButton.setPrefWidth(100);
        removeButton.setFont(Font.font(20));
        hBox.getChildren().addAll(addButton,removeButton);

        getChildren().addAll(tableView,hBox);
    }

    public void add(User user) {
        users.add(user);
        tableView.getItems().add(user);
    }

    public void updateRowInDatabase(User user,String columnName,String newValue,String conditionColumn,String conditionValue)
    {
        String query = "UPDATE user SET "+ columnName+"=? where "+conditionColumn+"=?";//columName=newValue,condition=conditionValue
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,newValue);
            preparedStatement.setString(2,conditionValue);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Did not sign in to DB");
            ex.printStackTrace();
        }

    }

}
/*
lastNameColumn = new TableColumn<User, String>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        AllowingEditing(lastNameColumn);
*/
