package application.bookstore.controllers;

import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.models.Book;
import application.bookstore.models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class BookControllerIntegrationTest {

    @BeforeEach
    void setUp() throws Exception {
        try (Connection connection = DriverManager.getConnection(DatabaseConnector.JDBC_URL, DatabaseConnector.USER, DatabaseConnector.PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO book (ISBN, name, author, category, supplier, description, bookURL, quantity, original_price, selling_price) " +
                    "VALUES ('0000000000000', 'Test Book', 'Test Author', 'Fiction', 1, 'A test description', 'http://example.com/book1', 10, 100.00, 120.00)");
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection connection = DriverManager.getConnection(DatabaseConnector.JDBC_URL, DatabaseConnector.USER, DatabaseConnector.PASSWORD);
             Statement statement = connection.createStatement()) {

            // Delete associated records from SoldBookType
            statement.execute("DELETE FROM SoldBookType WHERE ISBN IN ('0000000000000', '0000000000456')");

            // Delete associated records from Bill
            statement.execute("DELETE FROM Bill WHERE orderId IN (SELECT orderId FROM SoldBookType WHERE ISBN IN ('0000000000000', '0000000000456'))");

            // Delete the test books
            statement.execute("DELETE FROM Book WHERE ISBN IN ('0000000000000', '0000000000456')");
        }
    }





    @Test
    void testDeleteBook() throws Exception {
        String isbn = "0000000000000";

        try (Connection connection = DriverManager.getConnection(DatabaseConnector.JDBC_URL, DatabaseConnector.USER, DatabaseConnector.PASSWORD)) {
            BookController.deleteBook(isbn, connection);
        }

        try (Connection connection = DriverManager.getConnection(DatabaseConnector.JDBC_URL, DatabaseConnector.USER, DatabaseConnector.PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM book WHERE ISBN = '0000000000000'")) {
            assertFalse(resultSet.next());
        }
    }

    @Test
    void testGenerateBillFileOutput() throws IOException {
        User testUser = new User(new SimpleStringProperty("Test"), new SimpleStringProperty("User"), new SimpleStringProperty("test@example.com"), new SimpleStringProperty("testUser"), new SimpleStringProperty("password"), new SimpleStringProperty("Male"), new SimpleStringProperty("Admin"));
        Book testBook = new Book("0000000000000", "Test Book", "Test Author", "Fiction", 1, "A test description", null, 100.00, 120.00, 10);
        testBook.setChosenQuantity(2);
        ObservableList<Book> selectedBooks = FXCollections.observableArrayList(testBook);
        double totalAmount = 240.00;

        BookController.generateBill(testUser, selectedBooks, totalAmount);

        File billsFolder = new File("bills");
        assertTrue(billsFolder.exists());

        File[] billFiles = billsFolder.listFiles();
        assertNotNull(billFiles);
        assertTrue(billFiles.length > 0);

        File latestBill = billFiles[billFiles.length - 1];
        String billContent = Files.readString(latestBill.toPath());
        assertEquals(billContent,"Sold by: Test User Role: Admin\n" +
                "\n" +
                "Books:\n" +
                "- Test Book (Chosen Quantity: 2): $120.0Test Book - $120.0\n" +
                "\n" +
                "Total Amount: $240.0");
    }

    @Test
    void testUpdateQuantityWithMultipleBooks() throws Exception {
        Book book1 = new Book("0000000000000", "Test Book 1", "Author 1", "Fiction", 1, "Description 1", null, 100.00, 120.00, 10);
        Book book2 = new Book("0000000000456", "Test Book 2", "Author 2", "Science", 1, "Description 2", null, 150.00, 180.00, 20);

        book1.setChosenQuantity(2);
        book2.setChosenQuantity(5);

        try (Connection connection = DriverManager.getConnection(DatabaseConnector.JDBC_URL, DatabaseConnector.USER, DatabaseConnector.PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO book (ISBN, name, author, category, supplier, description, bookURL, quantity, original_price, selling_price) " +
                    "VALUES ('0000000000456', 'Test Book 2', 'Author 2', 'Science', 1, 'Description 2', 'http://example.com/book2', 20, 150.00, 180.00)");
        }

        ObservableList<Book> selectedBooks = FXCollections.observableArrayList(book1, book2);

        BookController.updateQuantity(selectedBooks);

        try (Connection connection = DriverManager.getConnection(DatabaseConnector.JDBC_URL, DatabaseConnector.USER, DatabaseConnector.PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT ISBN, quantity FROM book WHERE ISBN IN ('0000000000000', '0000000000456')")) {

            while (resultSet.next()) {
                String isbn = resultSet.getString("ISBN");
                int quantity = resultSet.getInt("quantity");

                if (isbn.equals("0000000000000")) {
                    assertEquals(8, quantity);
                } else if (isbn.equals("0000000000456")) {
                    assertEquals(15, quantity);
                }
            }
        }
    }

    @Test
    void testGenerateBillToDatabaseWithEmptyList() {
        ObservableList<Book> selectedBooks = FXCollections.observableArrayList();
        User testUser = new User(new SimpleStringProperty("Test"), new SimpleStringProperty("User"), new SimpleStringProperty("test@example.com"), new SimpleStringProperty("testUser"), new SimpleStringProperty("password"), new SimpleStringProperty("Male"), new SimpleStringProperty("Admin"));
        double totalAmount = 0.00;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            BookController.generateBillToDatabase(selectedBooks, totalAmount, testUser);
        });
        assertEquals("The selectedBooks list cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testGenerateBillToDatabaseWithNegativeAmount() {
        ObservableList<Book> selectedBooks = FXCollections.observableArrayList(new Book("0000000000000", "Test Book 1", "Author 1", "Fiction", 1, "Description 1", null, 100.00, 120.00, 10));
        User testUser = new User(new SimpleStringProperty("Test"), new SimpleStringProperty("User"), new SimpleStringProperty("test@example.com"), new SimpleStringProperty("testUser"), new SimpleStringProperty("password"), new SimpleStringProperty("Male"), new SimpleStringProperty("Admin"));
        double totalAmount = -10.00;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            BookController.generateBillToDatabase(selectedBooks, totalAmount, testUser);
        });
        assertEquals("Amount cannot be negative", exception.getMessage());
    }

    @Test
    void testGenerateBillToDatabaseWithValidInputs() throws Exception {
        ObservableList<Book> selectedBooks = FXCollections.observableArrayList(
                new Book("0000000000000", "Test Book", "Test Author", "Fiction", 1, "Description", null, 100.00, 120.00, 1)
        );
        selectedBooks.get(0).setChosenQuantity(1);
        User testUser = new User(new SimpleStringProperty("Test"), new SimpleStringProperty("User"), new SimpleStringProperty("test@example.com"), new SimpleStringProperty("testUser"), new SimpleStringProperty("password"), new SimpleStringProperty("Male"), new SimpleStringProperty("Admin"));
        double totalAmount = 120.00;

        BookController.generateBillToDatabase(selectedBooks, totalAmount, testUser);

        try (Connection connection = DriverManager.getConnection(DatabaseConnector.JDBC_URL, DatabaseConnector.USER, DatabaseConnector.PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Bill WHERE username = 'testUser'")) {
            assertTrue(resultSet.next());
            assertEquals("testUser", resultSet.getString("username"));
            assertEquals(totalAmount, resultSet.getDouble("total_amount"));
        }

        try (Connection connection = DriverManager.getConnection(DatabaseConnector.JDBC_URL, DatabaseConnector.USER, DatabaseConnector.PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM SoldBookType WHERE ISBN = '0000000000000'")) {

            assertTrue(resultSet.next());
            assertEquals(1, resultSet.getInt("soldQuantity"));
        }
    }

}

