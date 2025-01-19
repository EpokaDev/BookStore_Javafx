package application.bookstore.controllers;

import application.bookstore.Main;
import application.bookstore.views.AddBookView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class AddBookSystemTesting extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        Main main = new Main();
        main.start(stage);
    }



    @Test
    public void testAddBookWithValidInputs() throws Exception {
        System.out.println("Starting test: Add Book with Valid Inputs");

        // Simulate entering admin credentials
        clickOn("#userTextField");
        write("admin");

        clickOn("#passwordField");
        write("admin");

        // Simulate clicking the login button
        clickOn("#loginButton");

        // Verify the alert dialog content
        verifyThat(".alert .content", hasText("Login Successful!"));

        // Close the alert dialog
        clickOn(".alert .button");

        // Navigate to Add Book view by clicking the appropriate button in AdminView
        clickOn("#bookstoreButton"); // Assuming the button ID is "bookstoreButton" for navigation

        // Verify the Add Book screen is visible
        verifyThat("Add Book", isVisible());

        // Navigate to Add Book view
        clickOn("#addBookButton");

        // Generate a valid random 13-digit ISBN
        Random random = new Random();
        String validIsbn = "978" + String.format("%010d", random.nextInt(1_000_000_000));
        verifyThat("#isbnTextField", isVisible());
        clickOn("#isbnTextField").write(validIsbn);

        // Fill in other valid details
        clickOn("#titleTextField").write("Valid Book Title");
        clickOn("#authorTextField").write("Valid Author");
        clickOn("#categoryTextField").write("Fiction");
        clickOn("#descriptionTextField").write("This is a valid description.");
        clickOn("#originalPriceTextField").write("20.00");
        clickOn("#sellingPriceTextField").write("25.00");
        clickOn("#quantityTextField").write("100");
        clickOn("#supplierNameTextField").write("Valid Supplier");
        clickOn("#supplierEmailTextField").write("supplier@example.com");
        clickOn("#supplierPhoneTextField").write("1234567890");
        clickOn("#supplierAddressTextField").write("123 Valid Street");

        // Simulate setting an image file
        File testImage = new File("/Users/regiloshi/Downloads/Unknown-5.png");
        AddBookView addBookView = (AddBookView) FxToolkit.setupFixture(AddBookView::new);
        addBookView.selectedImageFile = testImage;

        // Submit the form
        clickOn("#confirm-Book");

        clickOn("#goBack");

        clickOn("#bookstoreButton");

        sleep(3000);

        System.out.println("Ending test: Add Book with Valid Inputs");
    }


}



