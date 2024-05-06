package application.bookstore.views;

import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class View {
    protected abstract Scene showView(Stage stage) throws Exception;

}
