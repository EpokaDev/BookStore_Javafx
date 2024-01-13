package application.bookstore.views;

//The admin log in info will be given at the beginning by the programmers
//Than the admin can change the username and password if they wish to

import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.models.Role;
import application.bookstore.models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.sql.*;


public class LoginView implements DatabaseConnector {
    User user;
    public Scene showView(Stage stage)
    {
        stage.setTitle("JavaFX Login");
        GridPane grid=new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text welcomeMessage = new Text("Welcome to the Bookstore");
        welcomeMessage.setTextAlignment(TextAlignment.CENTER);
        welcomeMessage.setFont(Font.font("Arial", FontWeight.NORMAL, 50));
        grid.add(welcomeMessage,0,0,2,2);

        Label userName = new Label("Username:");
        userName.setFont(Font.font(20));
        grid.add(userName, 0,4 );

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 4);

        Label password_label = new Label("Password:");
        password_label.setFont(Font.font(20));
        grid.add(password_label, 0, 5);

        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 5);

        Button btn = new Button("Log in");
        btn.setFont(Font.font(20));
        HBox hbtn=new HBox(btn);
        hbtn.setAlignment(Pos.CENTER);
        grid.add(hbtn, 0, 7,2,1);

        btn.setOnAction(e -> {
            String username1 = userTextField.getText().toString();
            String password1 = passwordField.getText().toString();

            if (userTextField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Form Error!",
                        "Please enter your username");
                return;
            }
            if (passwordField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Form Error!",
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
                                infoBox("Login Successful!", null, "Success");
                                application.bookstore.views.AdminView adminView = new application.bookstore.views.AdminView(user);
                                try {
                                    stage.setScene(adminView.showView(stage));
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
                }
            } catch (SQLException ex) {
                System.out.println("Did not sign in to DB");
                ex.printStackTrace();
            }
        });

        stage.setTitle("Log in");
        return new Scene(grid, 1000, 700);
    }
    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    public static void infoBox(String infoMessage, String headerText, String title) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }
}
