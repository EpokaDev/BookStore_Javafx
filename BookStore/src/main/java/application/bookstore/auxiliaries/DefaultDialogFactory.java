package application.bookstore.auxiliaries;

import application.bookstore.models.User;
import application.bookstore.views.AddNewUserDialog;
import javafx.scene.control.Dialog;

public class DefaultDialogFactory implements DialogFactory {
    @Override
    public Dialog<User> createAddUserDialog(User user) {
        return new AddNewUserDialog(user);
    }
}

