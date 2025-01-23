package application.bookstore.auxiliaries;

import application.bookstore.models.User;
import javafx.scene.control.Dialog;

public interface DialogFactory {
    Dialog<User> createAddUserDialog(User user);
}
