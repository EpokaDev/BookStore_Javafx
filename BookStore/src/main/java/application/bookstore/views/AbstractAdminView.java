package application.bookstore.views;

import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class AbstractAdminView {
    protected abstract Scene showView(Stage stage);
    protected abstract void getButtonImages();

}
