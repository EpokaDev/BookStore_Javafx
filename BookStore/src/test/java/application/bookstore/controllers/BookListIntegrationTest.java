package application.bookstore.controllers;

import application.bookstore.models.Book;
import javafx.scene.control.Alert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.ArrayList;
import javafx.application.Platform;
import org.testfx.util.WaitForAsyncUtils;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import java.util.concurrent.TimeUnit;

@Execution(ExecutionMode.SAME_THREAD)
class BookListIntegrationTest {



      /*
       -Integration testing between  BookList and BookController.
       -This also includes database connectivity, UI Components (JavaFX) -> Verifies that Alert dialogs and notifications triggered by BookList.notifyLowQuantity() interact with the JavaFX framework properly.
        -Combines logic from models (e.g., Book, User), controllers (e.g., BookController), and external frameworks (e.g., JavaFX or SQL).

        **Being tested:
        Can the notifyLowQuantity method:
        Identify books with low stock levels from an internal list (booksWithLowQuantity)?
        Trigger the appropriate UI alerts using JavaFX?
     */

    @BeforeAll
    static void setupJavaFX() {
        try {
            if (!Platform.isFxApplicationThread()) {
                Platform.startup(() -> {});
            }
        } catch (IllegalStateException e) {
            // JavaFX is already running, so we can ignore this
        }
    }

    @Test
        void testLowQuantityNotification_TriggerAlert() {
            Platform.runLater(() -> {
                // Initialize low stock book
                Book lowStockBook = new Book("1234567890", "Low Stock Book", "Author", "Category", 1,
                        "Description", null, 10.0, 15.0, 2);

                BookList bookList = new BookList();
                bookList.booksWithLowQuantity.add(lowStockBook);

                bookList.notifyLowQuantity();

            });

            WaitForAsyncUtils.waitForFxEvents(); // Wait for JavaFX thread
        }
    }