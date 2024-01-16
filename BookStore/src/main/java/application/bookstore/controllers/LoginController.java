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
    public LoginController(LoginView view, Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        addListener(view);
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
                            switch (role) {
                                case "admin":
                                    Alerts.infoBox("Login Successful!", null, "Success");
                                    application.bookstore.views.AdminView adminView = new application.bookstore.views.AdminView(user);
                                    try {
                                        primaryStage.setScene(adminView.showView(primaryStage));
                                    } catch (Exception exception) {
                                        System.out.println("Error in adminView");
                                        exception.printStackTrace();
                                    }
                                    break;
                                /*
                            case "librarian":
                                infoBox("Login Successful!", null, "Success");
                                application.bookstore.views.AdminView LibrarianView = new application.bookstore.views.LibrarianView(user);
                                try {
                                    stage.setScene(LibrarianView.showView(stage));
                                } catch (Exception exception) {
                                    System.out.println("Error in adminView");
                                    exception.printStackTrace();
                                }
                                break;
                            case "manager":
                                infoBox("Login Successful!", null, "Success");
                                application.bookstore.views.AdminView ManagerView = new application.bookstore.views.ManagerView(user);
                                try {
                                    stage.setScene(ManagerView.showView(stage));
                                } catch (Exception exception) {
                                    System.out.println("Error in adminView");
                                    exception.printStackTrace();
                                }
                                break;

                        }
                        */
                            }
                        }
                    }else Alerts.showAlert(Alert.AlertType.ERROR,"Incorrect username or password",
                            "Please enter the correct username and password");
                } catch (SQLException ex) {
                    System.out.println("Did not sign in to DB");
                    ex.printStackTrace();
                }
            });
    }
}
