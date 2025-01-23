package application.bookstore.controllers;

import application.bookstore.Main;
import application.bookstore.models.Book;
import application.bookstore.views.AddBookView;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookSystemTesting extends ApplicationTest {
//
//    public TableView<Book> tableView;
//
//    @Override
//    public void start(Stage stage) {
//        Main main = new Main();
//        main.start(stage);
//    }
//
//
//    @Order(1)
//    @Test
//    public void testAddBookWithValidInputs() throws Exception {
//        // Simulate entering admin credentials
//        clickOn("#userTextField");
//        write("admin");
//
//        clickOn("#passwordField");
//        write("admin");
//
//        // Simulate clicking the login button
//        clickOn("#loginButton");
//
//        // Verify the alert dialog content
//        verifyThat(".alert .content", hasText("Login Successful!"));
//
//        // Close the alert dialog
//        clickOn(".alert .button");
//
//        // Navigate to Add Book view by clicking the appropriate button in AdminView
//        clickOn("#bookstoreButton");
//
//        // Verify the Add Book screen is visible
//        verifyThat("Add Book", isVisible());
//
//        // Navigate to Add Book view
//        clickOn("#addBookButton");
//
//        verifyThat("#isbnTextField", isVisible());
//        clickOn("#isbnTextField").write("0000000000000");
//
//        // Fill in other valid details
//        clickOn("#titleTextField").write("Valid Book Title");
//        clickOn("#authorTextField").write("Valid Author");
//        clickOn("#categoryTextField").write("TestFiction");
//        clickOn("#descriptionTextField").write("This is a valid description.");
//        clickOn("#originalPriceTextField").write("20.00");
//        clickOn("#sellingPriceTextField").write("25.00");
//        clickOn("#quantityTextField").write("100");
//        clickOn("#supplierNameTextField").write("Valid Supplier");
//        clickOn("#supplierEmailTextField").write("supplier@example.com");
//        clickOn("#supplierPhoneTextField").write("1234567890");
//        clickOn("#supplierAddressTextField").write("123 Valid Street");
//
//        // Simulate setting an image file
//        File testImage = new File("/Users/regiloshi/Downloads/Unknown-5.png");
//        AddBookView addBookView = (AddBookView) FxToolkit.setupFixture(AddBookView::new);
//        addBookView.selectedImageFile = testImage;
//
//        // Submit the form
//        clickOn("#confirm-Book");
//
//        clickOn("#goBack");
//
//        clickOn("#bookstoreButton");
//
//        sleep(3000);
//
//    }
//    @Order(2)
//    @Test
//    public void testAddBookWithInvalidISBN() throws Exception {
//        // Simulate entering admin credentials
//        clickOn("#userTextField");
//        write("admin");
//
//        clickOn("#passwordField");
//        write("admin");
//
//        // Simulate clicking the login button
//        clickOn("#loginButton");
//
//        // Verify the alert dialog content
//        verifyThat(".alert .content", hasText("Login Successful!"));
//
//        // Close the alert dialog
//        clickOn(".alert .button");
//
//        // Navigate to Add Book view by clicking the appropriate button in AdminView
//        clickOn("#bookstoreButton");
//
//        // Verify the Add Book screen is visible
//        verifyThat("Add Book", isVisible());
//
//        // Navigate to Add Book view
//        clickOn("#addBookButton");
//
//        verifyThat("#isbnTextField", isVisible());
//        clickOn("#isbnTextField").write("00");
//
//        // Fill in other valid details
//        clickOn("#titleTextField").write("Valid Book Title");
//        clickOn("#authorTextField").write("Valid Author");
//        clickOn("#categoryTextField").write("Fiction");
//        clickOn("#descriptionTextField").write("This is a valid description.");
//        clickOn("#originalPriceTextField").write("20.00");
//        clickOn("#sellingPriceTextField").write("25.00");
//        clickOn("#quantityTextField").write("100");
//        clickOn("#supplierNameTextField").write("Valid Supplier");
//        clickOn("#supplierEmailTextField").write("supplier@example.com");
//        clickOn("#supplierPhoneTextField").write("1234567890");
//        clickOn("#supplierAddressTextField").write("123 Valid Street");
//
//        // Simulate setting an image file
//        File testImage = new File("/Users/regiloshi/Downloads/Unknown-5.png");
//        AddBookView addBookView = (AddBookView) FxToolkit.setupFixture(AddBookView::new);
//        addBookView.selectedImageFile = testImage;
//
//        // Submit the form
//        clickOn("#confirm-Book");
//        sleep(3000);
//    }
//    @Order(3)
//    @Test
//    public void testAddBookWithInvalidSupplierEmail() throws Exception {
//        // Simulate entering admin credentials
//        clickOn("#userTextField");
//        write("admin");
//
//        clickOn("#passwordField");
//        write("admin");
//
//        // Simulate clicking the login button
//        clickOn("#loginButton");
//
//        // Verify the alert dialog content
//        verifyThat(".alert .content", hasText("Login Successful!"));
//
//        // Close the alert dialog
//        clickOn(".alert .button");
//
//        // Navigate to Add Book view by clicking the appropriate button in AdminView
//        clickOn("#bookstoreButton");
//
//        // Verify the Add Book screen is visible
//        verifyThat("Add Book", isVisible());
//
//        // Navigate to Add Book view
//        clickOn("#addBookButton");
//
//        verifyThat("#isbnTextField", isVisible());
//        clickOn("#isbnTextField").write("0000000000000");
//
//        // Fill in other valid details
//        clickOn("#titleTextField").write("Valid Book Title");
//        clickOn("#authorTextField").write("Valid Author");
//        clickOn("#categoryTextField").write("Fiction");
//        clickOn("#descriptionTextField").write("This is a valid description.");
//        clickOn("#originalPriceTextField").write("20.00");
//        clickOn("#sellingPriceTextField").write("25.00");
//        clickOn("#quantityTextField").write("100");
//        clickOn("#supplierNameTextField").write("Valid Supplier");
//        clickOn("#supplierEmailTextField").write("aa");
//        clickOn("#supplierPhoneTextField").write("1234567890");
//        clickOn("#supplierAddressTextField").write("123 Valid Street");
//
//        // Simulate setting an image file
//        File testImage = new File("/Users/regiloshi/Downloads/Unknown-5.png");
//        AddBookView addBookView = (AddBookView) FxToolkit.setupFixture(AddBookView::new);
//        addBookView.selectedImageFile = testImage;
//
//        // Submit the form
//        clickOn("#confirm-Book");
//        sleep(3000);
//    }
//    @Order(4)
//    @Test
//    public void testAddBookWithInvalidSupplierPhoneNumber() throws Exception {
//
//        // Simulate entering admin credentials
//        clickOn("#userTextField");
//        write("admin");
//
//        clickOn("#passwordField");
//        write("admin");
//
//        // Simulate clicking the login button
//        clickOn("#loginButton");
//
//        // Verify the alert dialog content
//        verifyThat(".alert .content", hasText("Login Successful!"));
//
//        // Close the alert dialog
//        clickOn(".alert .button");
//
//        // Navigate to Add Book view by clicking the appropriate button in AdminView
//        clickOn("#bookstoreButton");
//
//        // Verify the Add Book screen is visible
//        verifyThat("Add Book", isVisible());
//
//        // Navigate to Add Book view
//        clickOn("#addBookButton");
//
//        verifyThat("#isbnTextField", isVisible());
//        clickOn("#isbnTextField").write("0000000000000");
//
//        // Fill in other valid details
//        clickOn("#titleTextField").write("Valid Book Title");
//        clickOn("#authorTextField").write("Valid Author");
//        clickOn("#categoryTextField").write("Fiction");
//        clickOn("#descriptionTextField").write("This is a valid description.");
//        clickOn("#originalPriceTextField").write("20.00");
//        clickOn("#sellingPriceTextField").write("25.00");
//        clickOn("#quantityTextField").write("100");
//        clickOn("#supplierNameTextField").write("Valid Supplier");
//        clickOn("#supplierEmailTextField").write("supplier@example.com");
//        clickOn("#supplierPhoneTextField").write("1");
//        clickOn("#supplierAddressTextField").write("123 Valid Street");
//
//        // Simulate setting an image file
//        File testImage = new File("/Users/regiloshi/Downloads/Unknown-5.png");
//        AddBookView addBookView = (AddBookView) FxToolkit.setupFixture(AddBookView::new);
//        addBookView.selectedImageFile = testImage;
//
//        // Submit the form
//        clickOn("#confirm-Book");
//        sleep(3000);
//    }
//
//   @Order(6)
//    @Test
//    public void testEditBookButton() {
//
//        // Simulate entering admin credentials
//        clickOn("#userTextField");
//        write("admin");
//
//        clickOn("#passwordField");
//        write("admin");
//
//        // Simulate clicking the login button
//        clickOn("#loginButton");
//
//        // Verify the alert dialog content
//        verifyThat(".alert .content", hasText("Login Successful!"));
//
//        // Close the alert dialog
//        clickOn(".alert .button");
//
//        // Navigate to Add Book view by clicking the appropriate button in AdminView
//        clickOn("#bookstoreButton");
//
//        tableView = lookup("#tableView").query();
//
//        clickOn("#0000000000000");
//        clickOn("#0000000000000");
//
//
//        // Verify that the edit and delete buttons are displayed
//        verifyThat("#editButton", isVisible());
//        verifyThat("#deleteButton", isVisible());
//
//
//        clickOn("#editButton");
//
//        clickOn("#titleTextField");
//        write("newTestTitle");
//        clickOn("#authorTextField");
//        write("newTestAuthor");
//        clickOn("#categoryTextField");
//        write("newTestCategory");
//        clickOn("#descriptionTextField");
//        write("newTestDescription");
//        clickOn("#originalPriceTextField");
//        write("55");
//        clickOn("#sellingPriceTextField");
//        write("99");
//        clickOn("#quantityTextField");
//        write("5");
//        clickOn("#supplierNameTextField");
//        write("TestSupplier");
//        clickOn("#supplierEmailTextField");
//        write("TestEmail@example.com");
//        clickOn("#supplierPhoneTextField");
//        write("0987654321");
//        clickOn("#supplierAddressTextField");
//        write("supplierAddressTextField");
//
//        clickOn("#confirmEditButton");
//
//        clickOn("#goBack");
//
//        clickOn("#bookstoreButton");
//
//        sleep(3000);
//
//    }
////
////
////    @Order(7)
////    @Test
////    public void testGenerateBill() throws Exception {
////        // Simulate entering admin credentials
////        clickOn("#userTextField").write("admin");
////        clickOn("#passwordField").write("admin");
////        clickOn("#loginButton");
////        verifyThat(".alert", isVisible());
////        clickOn(".alert .button");
////
////        // Navigate to bookstore
////        clickOn("#bookstoreButton");
////
////        // Locate and select the checkbox
////        clickOn("#checkbox-0000000000000");
////
////        // Locate the ChoiceBox and set quantity
////        clickOn("#quantity-choice-0000000000000");
////        clickOn("12");
////
////        clickOn("#generateBill");
////
////    }
//
//        @Order(8)
//    @Test
//    public void testClearButton() throws Exception {
//        // Simulate entering admin credentials
//        clickOn("#userTextField").write("admin");
//        clickOn("#passwordField").write("admin");
//        clickOn("#loginButton");
//        verifyThat(".alert", isVisible());
//        clickOn(".alert .button");
//
//        // Navigate to bookstore
//        clickOn("#bookstoreButton");
//
//        // Locate and select the checkbox
//        interact(() -> clickOn("#checkbox-0000000000000"));
//
//        // Locate the ChoiceBox and set quantity
//        ChoiceBox<Integer> choiceBox = lookup("#quantity-choice-0000000000000").query();
//        interact(() -> choiceBox.setValue(2));
//
//        clickOn("#clearButton");
//        TableView<Book> buying_tableView = lookup("#buying_tableView").query();
//        assertEquals(buying_tableView.getItems().size(), 0);
//    }
//
//    @Order(9)
//    @Test
//    public void testFilter() throws Exception {
//        // Simulate entering admin credentials
//        clickOn("#userTextField").write("admin");
//        clickOn("#passwordField").write("admin");
//        clickOn("#loginButton");
//        verifyThat(".alert", isVisible());
//        clickOn(".alert .button");
//
//        // Navigate to bookstore
//        clickOn("#bookstoreButton");
//
//        clickOn("#filter-combo-box");
//
//
//        clickOn("#filter-item-newTestCategory");
//
//        TableView<Book> bookTableView = lookup("#tableView").query();
//        sleep(2000);
//        System.out.println(bookTableView.getItems());
//        assertEquals(bookTableView.getItems().size(), 1);
//    }
//
//    @Order(10)
//    @Test
//    public void testSearch() throws Exception {
//        // Simulate entering admin credentials
//        clickOn("#userTextField").write("admin");
//        clickOn("#passwordField").write("admin");
//        clickOn("#loginButton");
//        verifyThat(".alert", isVisible());
//        clickOn(".alert .button");
//
//        // Navigate to bookstore
//        clickOn("#bookstoreButton");
//
//        clickOn("#search_field");
//        write("newTestTitle");
//
//        clickOn("#search-button");
//
//        TableView<Book> bookTableView = lookup("#tableView").query();
//        assertEquals(bookTableView.getItems().size(), 1);
//    }
//
//
//
//       @Order(11)
//        @Test
//    public void testDeleteBookButton() {
//
//        // Simulate entering admin credentials
//        clickOn("#userTextField");
//        write("admin");
//
//        clickOn("#passwordField");
//        write("admin");
//
//        // Simulate clicking the login button
//        clickOn("#loginButton");
//
//        // Verify the alert dialog content
//        verifyThat(".alert .content", hasText("Login Successful!"));
//
//        // Close the alert dialog
//        clickOn(".alert .button");
//
//        // Navigate to Add Book view by clicking the appropriate button in AdminView
//        clickOn("#bookstoreButton");
//
//        tableView = lookup("#tableView").query();
//
//        clickOn("#0000000000000");
//        clickOn("#0000000000000");
//
//        // Verify that the edit and delete buttons are displayed
//        verifyThat("#editButton", isVisible());
//        verifyThat("#deleteButton", isVisible());
//
//
//        clickOn("#deleteButton");
//
//        sleep(3000);
//
//    }


}



