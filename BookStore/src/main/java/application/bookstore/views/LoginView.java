package application.bookstore.views;

//The admin log in info will be given at the beginning by the programmers
//Than the admin can change the username and password if they wish to

import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.controllers.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class LoginView implements DatabaseConnector {
    private GridPane grid;
    private Button btn;
    private TextField userTextField;
    private PasswordField passwordField;

    public Scene showView(Stage stage) {
        StackPane root = new StackPane();

        try {
            FileInputStream logoFile;
            FileInputStream backgroundFile;

            String osName = System.getProperty("os.name").toLowerCase();

            if (osName.contains("win")) {
                logoFile = new FileInputStream("C:\\Users\\alvin\\OneDrive\\Desktop\\SW_testing\\Bookstore_javfx_testing\\BookStore_Javafx\\BookStore\\Images\\bookStoreLogo2.png");
                backgroundFile = new FileInputStream("C:\\Users\\alvin\\OneDrive\\Desktop\\SW_testing\\Bookstore_javfx_testing\\BookStore_Javafx\\BookStore\\Images\\3d-render-wooden-table-looking-out-blurred-background-with-bookcase.jpg");
            } else {
                logoFile = new FileInputStream("/Users/regiloshi/Documents/Documents - Regi’s MacBook Air (2)/Projects/BookStore_Javafx/BookStore/Images/bookStoreLogo2.png");
                backgroundFile = new FileInputStream("/Users/regiloshi/Documents/Documents - Regi’s MacBook Air (2)/Projects/BookStore_Javafx/BookStore/Images/3d-render-wooden-table-looking-out-blurred-background-with-bookcase.jpg");
            }

            Image logoImage = new Image(logoFile);
            Image backgroundImage = new Image(backgroundFile);

            ImageView logo = new ImageView(logoImage);
            ImageView background = new ImageView(backgroundImage);

            StackPane.setAlignment(logo, Pos.TOP_CENTER);
            background.setFitWidth(1000);
            background.setFitHeight(700);

            root.getChildren().addAll(background, logo);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text welcomeMessage = new Text("Welcome to the Bookstore");
        welcomeMessage.setFill(javafx.scene.paint.Color.WHITE);
        welcomeMessage.setTextAlignment(TextAlignment.CENTER);
        welcomeMessage.setFont(Font.font("Arial", FontWeight.NORMAL, 50));
        grid.add(welcomeMessage, 0, 0, 2, 2);

        Label userName = new Label("Username:");
        userName.setTextFill(javafx.scene.paint.Color.WHITE);
        userName.setFont(Font.font(20));
        grid.add(userName, 0, 4);

        userTextField = new TextField();
        userTextField.setId("userTextField"); //  fx:id
        grid.add(userTextField, 1, 4);

        Label password_label = new Label("Password:");
        password_label.setTextFill(javafx.scene.paint.Color.WHITE);
        password_label.setFont(Font.font(20));
        grid.add(password_label, 0, 5);

        passwordField = new PasswordField();
        passwordField.setId("passwordField"); //  fx:id
        grid.add(passwordField, 1, 5);

        btn = new Button("Log in");
        btn.setId("loginButton"); //  fx:id
        btn.setStyle("-fx-background-color: #808080;");
        btn.setTextFill(Color.WHITE);
        btn.setFont(Font.font(20));
        HBox hbtn = new HBox(btn);
        hbtn.setAlignment(Pos.CENTER);
        grid.add(hbtn, 0, 7, 2, 1);

        stage.setTitle("Log in");

        root.getChildren().add(grid);

        new LoginController(this, stage);

        root.setStyle(
                "-fx-font-family: 'JetBrains Mono'; " +
                        "-fx-font-size: 20px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-repeat: no-repeat; " +
                        "-fx-background-size: cover;"
        );

        return new Scene(root, 1000, 700);
    }

    public GridPane getMainPane() {
        return this.grid;
    }

    public Button getBtn() {
        return btn;
    }

    public TextField getUserTextField() {
        return userTextField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }
}
