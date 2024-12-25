package application.bookstore;

import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.views.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application implements DatabaseConnector {

    public void start(Stage stage)  {
        LoginView lg=new LoginView();
        stage.setTitle("Bookstore");

        stage.setScene(lg.showView(stage));
        stage.show();

    }
    public static void main(String[] args) {
        launch();
    }

}
