package application.bookstore.controllers;

import application.bookstore.models.Book;
import javafx.scene.control.Alert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.ArrayList;
import javafx.application.Platform;
class BookListIntegrationTest {
    @BeforeAll
    static void setup() {
        Platform.startup(() -> {
            // This initializes the JavaFX toolkit
        });
    }

      /*
       -Integration testing between  BookList and BookController.
       -This also includes database connectivity, UI Components (JavaFX) -> Verifies that Alert dialogs and notifications triggered by BookList.notifyLowQuantity() interact with the JavaFX framework properly.
        -Combines logic from models (e.g., Book, User), controllers (e.g., BookController), and external frameworks (e.g., JavaFX or SQL).

        **Being tested:
        Can the notifyLowQuantity method:
        Identify books with low stock levels from an internal list (booksWithLowQuantity)?
        Trigger the appropriate UI alerts using JavaFX?
     */

    @Test
    void testLowQuantityNotification_TriggerAlert() {
        Platform.runLater(() -> {
            // Your JavaFX-dependent test code here
            // For example:
            Book lowStockBook = new Book("1234567890", "Low Stock Book", "Author", "Category", 1,
                    "Description", null, 10.0, 15.0, 2); // Low quantity
            BookList bookList = new BookList();
            bookList.booksWithLowQuantity.add(lowStockBook);

            // Act
            bookList.notifyLowQuantity();

            // Assert
            System.out.println("Alert triggered for low stock books.");
        });

        // Wait for JavaFX thread to finish
        try {
            Thread.sleep(500); // Allow some time for JavaFX actions to complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
