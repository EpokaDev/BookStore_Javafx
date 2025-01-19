package application.bookstore.controllers;

import application.bookstore.Main;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class AddBookSystemTesting extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        // Call the main application entry point
        Main main = new Main();
        main.start(stage);
    }
    @Test
    public void testAdminLogin() {
        System.out.println("Starting test: Admin Login");

        // Add a delay to ensure UI elements are loaded
        sleep(1000);

        // Simulate entering username
        clickOn("#userTextField");
        write("admin");


        // Simulate clicking the login button
        clickOn("#loginButton");

        System.out.println("Ending test: Admin Login");
    }
    }


