package application.bookstore.controllers;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FilterControllerUnitTesting {

//    @BeforeAll
//    static void initJavaFX() {
//        if (!Platform.isFxApplicationThread()) {
//            Platform.startup(() -> {});
//        }
//    }
//
//
//    @Test
//    public void testCreateFilterComboBoxWithCategories() {
//        ArrayList<String> categories = new ArrayList<>();
//        categories.add("Fiction");
//        categories.add("Non-Fiction");
//        categories.add("Science");
//
//        ComboBox<String> comboBox = FilterController.createFilterComboBox(categories);
//
//        assertNotNull(comboBox, "ComboBox should not be null");
//        assertEquals(3, comboBox.getItems().size(), "ComboBox should contain 3 items");
//        assertEquals("Fiction", comboBox.getSelectionModel().getSelectedItem(), "First item should be selected by default");
//    }
//
//    @Test
//    public void testCreateFilterComboBoxWithEmptyCategories() {
//        ArrayList<String> categories = new ArrayList<>();
//
//        ComboBox<String> comboBox = FilterController.createFilterComboBox(categories);
//
//        assertNotNull(comboBox, "ComboBox should not be null");
//        assertTrue(comboBox.getItems().isEmpty(), "ComboBox should have no items");
//        assertNull(comboBox.getSelectionModel().getSelectedItem(), "No item should be selected for an empty ComboBox");
//    }
//
//    @Test
//    public void testCreateFilterComboBoxWithNullCategories() {
//        ComboBox<String> comboBox = FilterController.createFilterComboBox(null);
//
//        assertNotNull(comboBox, "ComboBox should not be null");
//        assertTrue(comboBox.getItems().isEmpty(), "ComboBox should have no items when categories is null");
//        assertNull(comboBox.getSelectionModel().getSelectedItem(), "No item should be selected for a ComboBox with null categories");
//    }
}

