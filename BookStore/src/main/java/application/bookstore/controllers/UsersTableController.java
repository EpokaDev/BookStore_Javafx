package application.bookstore.controllers;

import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.auxiliaries.DialogFactory;
import application.bookstore.models.User;
import application.bookstore.views.AddNewUserDialog;
import application.bookstore.views.UsersTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class UsersTableController implements DatabaseConnector {
    private final ObservableList<User> users;

    private final TableView<User> tableView;
    private final TableColumn<User, String> firstNameColumn;
    private final TableColumn<User, String> lastNameColumn;
    private final TableColumn<User, String> emailColumn;
    private final TableColumn<User, String> userNameColumn;
    private final TableColumn<User, String> passwordColumn;
    private final TableColumn<User, String> genderColumn;
    private final TableColumn<User, String> roleColumn;
    private final Button addButton;
    private final Button removeButton;

    private DataSource dataSource;

    private DialogFactory dialogFactory;

    private boolean testing=false;

    private Connection mockConnection;


    public UsersTableController(UsersTableView view, ObservableList<User> currentUsers) {

        this.tableView = view.getTableView();
        this.users = currentUsers;

        this.firstNameColumn = view.getFirstNameColumn();
        this.lastNameColumn = view.getLastNameColumn();
        this.emailColumn = view.getEmailColumn();
        this.userNameColumn = view.getUserNameColumn();
        this.passwordColumn = view.getPasswordColumn();
        this.genderColumn = view.getGenderColumn();
        this.roleColumn = view.getRoleColumn();

        this.addButton = view.getAddButton();
        this.removeButton = view.getRemoveButton();

        this.testing = false;

        System.out.println("hello");
        Listener(view);
    }

    public UsersTableController(UsersTableView view, ObservableList<User> users, DataSource dataSource,Connection mockConnection) {
        this.tableView = view.getTableView();
        this.users = users;
        this.dataSource = dataSource;

        this.firstNameColumn = view.getFirstNameColumn();
        this.lastNameColumn = view.getLastNameColumn();
        this.emailColumn = view.getEmailColumn();
        this.userNameColumn = view.getUserNameColumn();
        this.passwordColumn = view.getPasswordColumn();
        this.genderColumn = view.getGenderColumn();
        this.roleColumn = view.getRoleColumn();

        this.addButton = view.getAddButton();
        this.removeButton = view.getRemoveButton();

        this.testing = true;
        this.mockConnection = mockConnection;

        System.out.println("hello");
        Listener(view);
    }

    private void Listener(UsersTableView view) {
        // OnEditCommit for various columns
        firstNameColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setFirstName(event.getNewValue());
            updateRowInDatabase(user, "firstName", user.getFirstName(), "userName", user.getUsername());
        });

        lastNameColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setLastName(event.getNewValue());
            updateRowInDatabase(user, "lastName", user.getLastName(), "userName", user.getUsername());
        });

        emailColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setEmail(event.getNewValue());
            updateRowInDatabase(user, "email", user.getEmail(), "userName", user.getUsername());
        });

        userNameColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setUsername(event.getNewValue());
            // Notice the condition column is "password" because you want to match unique records
            // or however you identify them. Adjust as needed.
            updateRowInDatabase(user, "userName", user.getUsername(), "password", user.getPassword());
        });

        passwordColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setPassword(event.getNewValue());
            updateRowInDatabase(user, "password", user.getPassword(), "userName", user.getUsername());
        });

        genderColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setGender(event.getNewValue());
            updateRowInDatabase(user, "gender", user.getGender(), "userName", user.getUsername());
        });

        roleColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setRole(event.getNewValue());
            updateRowInDatabase(user, "Role", user.getRoleString(), "userName", user.getUsername());
        });

        // Add button
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Dialog<User> userDialog = new AddNewUserDialog(
                            new User(
                                    new SimpleStringProperty(""),
                                    new SimpleStringProperty(""),
                                    new SimpleStringProperty(""),
                                    new SimpleStringProperty(""),
                                    new SimpleStringProperty(""),
                                    new SimpleStringProperty(""),
                                    new SimpleStringProperty("")
                            )
                    );
                    Optional<User> result = userDialog.showAndWait();
                    System.out.println("Hello -> checking result");
                    if (result.isPresent()) {
                        User user = result.get();
                        System.out.println("Adding to DB");
                        add(user);
                    }
                } catch (Exception e) {
                    System.out.println("Something wrong with the dialog");
                    e.printStackTrace();
                }
            }
        });

        // Remove button
        removeButton.setOnAction(e -> {
            int row = tableView.getSelectionModel().getSelectedIndex();
            removeRow(row);
        });
    }

    public void updateRowInDatabase(User user, String columnName, String newValue,
                                    String conditionColumn, String conditionValue) {
        String query = "UPDATE user SET " + columnName + " = ? WHERE " + conditionColumn + " = ?";
        if(!testing)
        {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, newValue);
                preparedStatement.setString(2, conditionValue);
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                System.out.println("Did not sign in to DB");
                ex.printStackTrace();
            }
        }else
        {
            try (
                 PreparedStatement preparedStatement = mockConnection.prepareStatement(query)) {

                preparedStatement.setString(1, newValue);
                preparedStatement.setString(2, conditionValue);
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                System.out.println("Did not sign in to DB");
                ex.printStackTrace();
            }
        }

    }

    public void add(User user) {
        // 1) Add to the ObservableList (which also updates the table)
        users.add(user);

        System.out.println("Adding user");

        if(!testing)
        {
            // 2) Add to the database
            String query = "INSERT INTO user (firstName, lastName, email, userName, password, gender, Role) VALUES (?,?,?,?,?,?,?);";
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, user.getFirstName());
                preparedStatement.setString(2, user.getLastName());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setString(4, user.getUsername());
                preparedStatement.setString(5, user.getPassword());
                preparedStatement.setString(6, user.getGender());
                preparedStatement.setString(7, user.getRoleString());

                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                System.out.println("Problem when adding user");
                ex.printStackTrace();
            }
        }else
        {
            String query = "INSERT INTO user (firstName, lastName, email, userName, password, gender, Role) VALUES (?,?,?,?,?,?,?);";
            try (
                 PreparedStatement preparedStatement = mockConnection.prepareStatement(query)) {

                preparedStatement.setString(1, user.getFirstName());
                preparedStatement.setString(2, user.getLastName());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setString(4, user.getUsername());
                preparedStatement.setString(5, user.getPassword());
                preparedStatement.setString(6, user.getGender());
                preparedStatement.setString(7, user.getRoleString());

                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                System.out.println("Problem when adding user");
                ex.printStackTrace();
            }
        }

    }



    public void removeRow(int row) {
        if (row >= 0 && row < tableView.getItems().size()) {
            User userToRemove = tableView.getItems().get(row);

            if(!testing)
            {
                // 1) Remove from DB
                String query = "DELETE FROM user WHERE userName = ?";
                try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, userToRemove.getUsername());
                    preparedStatement.executeUpdate();
                } catch (SQLException ex) {
                    System.out.println("Did not sign in to DB");
                    ex.printStackTrace();
                }

                // 2) Remove from the list/table
                tableView.getItems().remove(row);
            }else
            {
                // 1) Remove from DB
                String query = "DELETE FROM user WHERE userName = ?";
                try (
                     PreparedStatement preparedStatement = mockConnection.prepareStatement(query)) {
                    preparedStatement.setString(1, userToRemove.getUsername());
                    preparedStatement.executeUpdate();
                } catch (SQLException ex) {
                    System.out.println("Did not sign in to DB");
                    ex.printStackTrace();
                }

                // 2) Remove from the list/table
                tableView.getItems().remove(row);
            }

        }
    }



    public ObservableList<User> returnUsers()
    {
        return this.users;
    }

    public int returnUsersSize()
    {
        System.out.println(users.size());
        System.out.println(users);
        return this.users.size();
    }
}
