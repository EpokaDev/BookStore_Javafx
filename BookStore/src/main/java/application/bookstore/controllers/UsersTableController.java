package application.bookstore.controllers;

import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.models.User;
import application.bookstore.views.AddNewUserDialog;
import application.bookstore.views.UsersTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class UsersTableController implements DatabaseConnector {
    private ObservableList<User> users;

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

    public UsersTableController(UsersTableView view,ObservableList<User> currentUsers) {

        this.tableView=view.getTableView();
        this.users=currentUsers;

        firstNameColumn=view.getFirstNameColumn();
        lastNameColumn=view.getLastNameColumn();
        emailColumn=view.getEmailColumn();
        userNameColumn=view.getUserNameColumn();
        passwordColumn=view.getPasswordColumn();
        genderColumn=view.getGenderColumn();
        roleColumn=view.getRoleColumn();

        addButton=view.getAddButton();
        removeButton=view.getRemoveButton();

        Listener(view);
    }

    private void Listener(UsersTableView view)
    {
        firstNameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> event) {
                User user = event.getRowValue();
                user.setFirstName(event.getNewValue());
                updateRowInDatabase(user,"firstName",user.getFirstName(),"userName", user.getUsername());
            }
        });

        lastNameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> event) {
                User user = event.getRowValue();
                user.setLastName(event.getNewValue());
                updateRowInDatabase(user,"lastName",user.getLastName(),"userName", user.getUsername());
            }
        });
        emailColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> event) {
                User user = event.getRowValue();
                user.setEmail(event.getNewValue());
                updateRowInDatabase(user,"email",user.getEmail(),"userName", user.getUsername());

            }
        });

        userNameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> event) {
                User user = event.getRowValue();
                user.setUsername(event.getNewValue());
                updateRowInDatabase(user,"userName",user.getUsername(),"password", user.getPassword());
                //only here we condtion with the password since we are changing the username
            }
        });

        passwordColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> event) {
                User user = event.getRowValue();
                user.setPassword(event.getNewValue());
                updateRowInDatabase(user,"password",user.getPassword(),"userName", user.getUsername());
            }
        });
        genderColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> event) {
                User user = event.getRowValue();
                user.setGender(event.getNewValue());
                updateRowInDatabase(user,"gender",user.getGender(),"userName", user.getUsername());
            }
        });
        roleColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> event) {
                User user = event.getRowValue();
                user.setRole(event.getNewValue());
                updateRowInDatabase(user,"Role",user.getRoleString(),"userName",user.getUsername());
            }
        });

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try
                {
                    Dialog<User> userDialog = new AddNewUserDialog(
                            new User(new SimpleStringProperty(""),new SimpleStringProperty(""),
                                    new SimpleStringProperty(""),new SimpleStringProperty(""),
                                    new SimpleStringProperty(""),new SimpleStringProperty(""),
                                    new SimpleStringProperty("")));
                    Optional<User> result = userDialog.showAndWait();
                    if (result.isPresent()) {
                        User user = result.get();
                        add(user);
                    }
                }catch (Exception e)
                {
                    System.out.println("Something wrong with the dialog");
                    e.printStackTrace();
                }

            }
        });

        removeButton.setOnAction(e->
        {
            int row = tableView.getSelectionModel().getSelectedIndex();

            if (row >= 0 && row < tableView.getItems().size()) {
                User u = tableView.getItems().get(row);

                //Removing from database
                String query = "DELETE FROM user where userName=?";
                try {
                    Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, u.getUsername());
                    int rowsAffected = preparedStatement.executeUpdate();
                } catch (SQLException ex) {
                    System.out.println("Did not sign in to DB");
                    ex.printStackTrace();
                }

                //removing from tableView and users
                removeRow(row);
            }
        });
}

    private void updateRowInDatabase(User user,String columnName,String newValue,String conditionColumn,String conditionValue) {
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
    public void add(User user) {
        //Adding user to the list
        users.add(user); //will also automatically be added to the tableView

        for(User u:users)
        {
            System.out.println(u);
        }

        //adding user to database
        String query="INSERT INTO user (firstName, lastName, email, userName, password, gender, Role) VALUES" +
                "(?,?,?,?,?,?,?);";
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,user.getFirstName());
            preparedStatement.setString(2,user.getLastName());
            preparedStatement.setString(3,user.getEmail());
            preparedStatement.setString(5,user.getPassword());
            preparedStatement.setString(4,user.getUsername());;
            preparedStatement.setString(6,user.getGender());
            preparedStatement.setString(7,user.getRoleString());

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Problem when adding user");
            ex.printStackTrace();
        }

    }
    public void removeRow(int row)
    {
        tableView.getItems().remove(row);
    }
    }
