package application.bookstore.controllers;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import java.util.ArrayList;

public class FilterController {

    public static ComboBox<String> createFilterComboBox(ArrayList<String> categories) {
        ComboBox<String> comboBox = new ComboBox<>();

        comboBox.setId("filter-combo-box");

        if (categories != null) {
            for (String category : categories) {
                comboBox.getItems().add(category);
            }
            if (!categories.isEmpty()) {
                comboBox.getSelectionModel().selectFirst();
            }

            comboBox.setCellFactory(listView -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        setId("filter-item-" + item);
                    }
                }
            });
        }

        return comboBox;
    }


}
