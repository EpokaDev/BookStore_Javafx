package application.bookstore.controllers;

import application.bookstore.auxiliaries.Alerts;
import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.models.User;
import application.bookstore.views.LoginView;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.sql.*;

public class LoginController implements DatabaseConnector {
    private final Stage primaryStage;
    private User user;

    private Connection connection; // <-- injected DB connection


    public LoginController(LoginView view, Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        addListener(view);
    }

    //will be used for testing
    public LoginController(LoginView view, Stage primaryStage, Connection connection) {
        this.primaryStage = primaryStage;
        this.connection = connection;
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        addListenerTesting(view);
    }

    private void addListener(LoginView view) {
        view.getMainPane().setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                view.getBtn().fire();
            }
        });
        view.getBtn().setOnAction(e -> {
            String username1 = view.getUserTextField().getText();
            String password1 = view.getPasswordField().getText();

            if (username1.isEmpty()) {
                Alerts.showAlert(Alert.AlertType.ERROR, "Form Error!",
                        "Please enter your username");
                return;
            }
            if (password1.isEmpty()) {
                Alerts.showAlert(Alert.AlertType.ERROR, "Form Error!",
                        "Please enter a password");
                return;
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException exe) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Error connecting to Database!");
                alert.showAndWait();
            }

            String query = "SELECT * FROM user WHERE username = ? AND password = ?";
            //? is a placeholder

            try {
                Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                //Binding parameters
                preparedStatement.setString(1, username1);
                preparedStatement.setString(2, password1);
                ResultSet resultSet = preparedStatement.executeQuery();

                //We expect only 1 row or 0 so if() is used instead of while
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    if (username1.equals(username) && password1.equals(password)) {
                        String role = resultSet.getString("Role").toLowerCase();
                        user = new User(
                                new SimpleStringProperty(resultSet.getString("firstName")),
                                new SimpleStringProperty(resultSet.getString("lastName")),
                                new SimpleStringProperty(resultSet.getString("email")),
                                new SimpleStringProperty(username),
                                new SimpleStringProperty(password),
                                new SimpleStringProperty(resultSet.getString("gender")),
                                new SimpleStringProperty(resultSet.getString("Role"))
                        );
                        if (user.getRoleString().equalsIgnoreCase("admin")) {
                            Alerts.infoBox("Login Successful!", null, "Success");
                            application.bookstore.views.AdminView adminView = new application.bookstore.views.AdminView(user);
                            try {
                                primaryStage.setScene(adminView.showView(primaryStage));
                            } catch (Exception exception) {
                                System.out.println("Error in adminView");
                                System.out.println(exception.getMessage());
                                exception.printStackTrace();
                            }
                        } else {
                            Alerts.infoBox("Login Successful!", null, "Success");
                            application.bookstore.views.BookView bookView = new application.bookstore.views.BookView(user.getRole() , user );
                            try {
                                primaryStage.setScene(bookView.showView(primaryStage));
                            } catch (Exception exception) {
                                System.out.println("Error in bookView");
                                System.out.println(exception.getMessage());
                                exception.printStackTrace();
                            }
                        }
                    } else {
                        Alerts.showAlert(Alert.AlertType.ERROR, "Incorrect username or password",
                                "Please enter the correct username and password");
                    }
                }
            } catch (SQLException ex) {
                System.out.println("Did not sign in to DB");
                System.out.println(ex.getMessage());
            }
        });
    }

    private void addListenerTesting(LoginView view) {
        view.getMainPane().setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                view.getBtn().fire();
            }
        });
        view.getBtn().setOnAction(e -> {
            String username1 = view.getUserTextField().getText();
            String password1 = view.getPasswordField().getText();

            if (username1.isEmpty()) {
                Alerts.showAlert(Alert.AlertType.ERROR, "Form Error!",
                        "Please enter your username");
                return;
            }
            if (password1.isEmpty()) {
                Alerts.showAlert(Alert.AlertType.ERROR, "Form Error!",
                        "Please enter a password");
                return;
            }

            // In production, we might load the DB driver:
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException exe) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Error connecting to Database!");
                alert.showAndWait();
                return;  // stop processing
            }

            String query = "SELECT * FROM user WHERE username = ? AND password = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username1);
                preparedStatement.setString(2, password1);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    if (username1.equals(username) && password1.equals(password)) {
                        String role = resultSet.getString("Role").toLowerCase();
                        user = new User(
                                new SimpleStringProperty(resultSet.getString("firstName")),
                                new SimpleStringProperty(resultSet.getString("lastName")),
                                new SimpleStringProperty(resultSet.getString("email")),
                                new SimpleStringProperty(username),
                                new SimpleStringProperty(password),
                                new SimpleStringProperty(resultSet.getString("gender")),
                                new SimpleStringProperty(resultSet.getString("Role"))
                        );
                        // Admin view or Book view?
                        if ("admin".equalsIgnoreCase(user.getRoleString())) {
                            Alerts.infoBox("Login Successful!", null, "Success");
                            application.bookstore.views.AdminView adminView =
                                    new application.bookstore.views.AdminView(user);
                            primaryStage.setScene(adminView.showView(primaryStage));
                        } else {
                            Alerts.infoBox("Login Successful!", null, "Success");
                            application.bookstore.views.BookView bookView =
                                    new application.bookstore.views.BookView(user.getRole(), user);
                            primaryStage.setScene(bookView.showView(primaryStage));
                        }
                    } else {
                        Alerts.showAlert(Alert.AlertType.ERROR, "Incorrect username or password",
                                "Please enter the correct username and password");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    // For testing: we can inspect which user got logged in
    public User getLoggedInUser() {
        return user;
    }
}
