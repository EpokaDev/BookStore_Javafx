package application.bookstore.controllers;

import javafx.scene.control.ComboBox;

import java.util.ArrayList;

public class FilterController {
    public static ComboBox<String> createFilterComboBox(ArrayList<String> categories) {
        ComboBox<String> comboBox = new ComboBox<>();
        if (categories != null) {
            for (String string : categories) {
                comboBox.getItems().add(string);
            }
            if (!categories.isEmpty()) {
                comboBox.getSelectionModel().selectFirst();
            }
        }
        return comboBox;
    }

}
