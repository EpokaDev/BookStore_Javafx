package application.bookstore.controllers;

import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.models.Book;
import application.bookstore.models.Supplier;
import application.bookstore.models.User;
import application.bookstore.views.AddBookView;
import application.bookstore.views.BookView;
import application.bookstore.views.EditBookView;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class BookViewIntegrationTest extends ApplicationTest implements DatabaseConnector {

    private BookView bookView;
    private User testUser;
    private static Connection connection;

    @Override
    public void start(Stage stage) throws Exception {
        testUser = new User(
                new SimpleStringProperty("John"),
                new SimpleStringProperty("Doe"),
                new SimpleStringProperty("john.doe@example.com"),
                new SimpleStringProperty("johndoe"),
                new SimpleStringProperty("password"),
                new SimpleStringProperty("Male"),
                new SimpleStringProperty("admin")
        );

        bookView = new BookView(new SimpleStringProperty("admin"), testUser);

        stage.setScene(bookView.showView(stage));
        stage.show();
    }

    @BeforeEach
    void setUp() throws Exception {
        // Establish a connection to the database
        connection = DriverManager.getConnection(DatabaseConnector.JDBC_URL, DatabaseConnector.USER, DatabaseConnector.PASSWORD);

        // Insert a test book into the database
        try (Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO book (ISBN, name, author, category, supplier, description, bookURL, quantity, original_price, selling_price) " +
                    "VALUES ('0000000000000', 'Test Book', 'Test Author', 'FictionTest', 1, 'A test description', 'http://example.com/book1', 10, 100.00, 120.00)");
            ResultSet resultSet = statement.executeQuery("SELECT * FROM book WHERE ISBN = '0000000000000'");
        }

    }


    @Test
    void testLoadBooksIntoTableView() {
        Platform.runLater(() -> {
            TableView<Book> tableView = bookView.getTableView();
            assertNotNull(tableView.getItems());
            assertFalse(tableView.getItems().isEmpty());
        });
    }

    @Test
    void testSearchFunctionality() {
        Platform.runLater(() -> {
            bookView.filterTable("FictionTest", "Test Book");
            TableView<Book> tableView = bookView.getTableView();
            for (Book book : tableView.getItems()) {
                assertTrue(book.getTitle().contains("Test Book"));
                assertEquals("Fiction", book.getCategory());
            }
        });
    }

    @Test
    void testAddBookFunctionality() {
        Platform.runLater(() -> {
            AddBookView addBookView = new AddBookView();
            Stage stage = new Stage();
            Scene scene = addBookView.showView(stage);
            stage.setScene(scene);
            stage.show();

            // Simulate user input
            addBookView.isbnTextField.setText("9780134685991");
            addBookView.titleTextField.setText("Effective Java");
            addBookView.authorTextField.setText("Joshua Bloch");
            addBookView.categoryTextField.setText("Programming");
            addBookView.descriptionTextField.setText("A comprehensive guide to Java programming.");
            addBookView.originalPriceTextField.setText("40.00");
            addBookView.sellingPriceTextField.setText("45.00");
            addBookView.quantityTextField.setText("5");
            addBookView.supplierNameTextField.setText("Valid Supplier");
            addBookView.supplierEmailTextField.setText("supplier@example.com");
            addBookView.supplierPhoneTextField.setText("1234567890");

            // Trigger the Add Book button action
            addBookView.createBook();

            // Verify the book is added to the database
            try (Connection connection = DriverManager.getConnection(DatabaseConnector.JDBC_URL, DatabaseConnector.USER, DatabaseConnector.PASSWORD);
                 Statement statement = connection.createStatement()) {
                var resultSet = statement.executeQuery("SELECT * FROM Book WHERE ISBN = '9780134685991'");
                assertTrue(resultSet.next());
                assertEquals("Effective Java", resultSet.getString("name"));
            } catch (Exception e) {
                fail("Database query failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRowSelectionInTableView() {
        Platform.runLater(() -> {
            TableView<Book> tableView = bookView.getTableView();
            Book book = new Book("9780134685991", "Effective Java", "Joshua Bloch", "Programming", "A guide to Java programming.",
                    null, 40.00, 45.00, 5);
            tableView.getItems().add(book);

            // Simulate a click on the checkbox
            for (TableColumn<Book, ?> column : tableView.getColumns()) {
                if (column.getText().isEmpty()) { // This identifies the selectCol
                    @SuppressWarnings("unchecked")
                    TableColumn<Book, CheckBox> selectCol = (TableColumn<Book, CheckBox>) column;
                    CheckBox checkBox = selectCol.getCellData(book);
                    checkBox.setSelected(true); // Simulates clicking the checkbox
                    break;
                }
            }

            // Verify the book is added to the selectedBooks list
            assertTrue(bookView.selectedBooks.contains(book), "Book should be in the selectedBooks list after selection");
        });
    }

    @Test
    void testGenerateBill() {
        Platform.runLater(() -> {
            // Add a book to the selectedBooks list
            Book book = new Book("0000000000001", "Effective Java", "Joshua Bloch", "Programming", "A guide to Java programming.",
                    null, 40.00, 45.00, 5);
            book.setImageUrl("http://example.com/book1.jpg");
            book.saveToDatabase();
            bookView.selectedBooks.add(book);
            book.setChosenQuantity(2);

            // Simulate bill generation
            Button generateBillButton = (Button) bookView.pane.lookup("#generateBill");
            generateBillButton.fire();

            // Verify database entries for the generated bill
            try (Connection connection = DriverManager.getConnection(DatabaseConnector.JDBC_URL, DatabaseConnector.USER, DatabaseConnector.PASSWORD);
                 Statement statement = connection.createStatement()) {
                var resultSet = statement.executeQuery("SELECT * FROM Bill WHERE total_Amount = 90.00"); // 2 * 45.00
                assertTrue(resultSet.next());
            } catch (Exception e) {
                fail("Database query failed: " + e.getMessage());
            }
        });
    }


    @Test
    void testFilterTable() {
        Platform.runLater(() -> {
            Book book1 = new Book("9780134685991", "Effective Java", "Joshua Bloch", "Programming", "A guide to Java programming.",
                    null, 40.00, 45.00, 5);
            Book book2 = new Book("9780321356680", "Clean Code", "Robert C. Martin", "Programming", "A guide to writing clean code.",
                    null, 30.00, 35.00, 10);
            bookView.getTableView().getItems().addAll(book1, book2);
            for(Book book : bookView.getTableView().getItems()) {
                System.out.println(book.getCategory());
            }
            // Filter by category
            bookView.filterTable("Programming", "");

            for(Book book : bookView.getTableView().getItems()) {
                System.out.println(book.getTitle());
            }

            TableView<Book> tableView = bookView.getTableView();
            assertEquals(2, tableView.getItems().size());

            // Filter by title
            bookView.filterTable("Programming", "Clean Code");
            assertEquals(1, tableView.getItems().size());
            assertEquals("Clean Code", tableView.getItems().get(0).getTitle());
        });
    }









    @AfterEach
    void tearDown() throws Exception {
        try (Connection connection = DriverManager.getConnection(DatabaseConnector.JDBC_URL, DatabaseConnector.USER, DatabaseConnector.PASSWORD);
             Statement statement = connection.createStatement()) {

            // Delete associated records from SoldBookType
            statement.execute("DELETE FROM SoldBookType WHERE ISBN IN ('0000000000000')");

            // Delete associated records from Bill
            statement.execute("DELETE FROM Bill WHERE orderId IN (SELECT orderId FROM SoldBookType WHERE ISBN IN ('0000000000000', '0000000000456'))");

            // Delete the test books
            statement.execute("DELETE FROM Book WHERE ISBN IN ('0000000000000')");
        }
    }
    @AfterAll
    static void cleanUpDatabase() {
        try {
            connection = DriverManager.getConnection(DatabaseConnector.JDBC_URL, DatabaseConnector.USER, DatabaseConnector.PASSWORD);

            // Remove the test book with ISBN 9780134685991
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM Book WHERE ISBN = '9780134685991'");
            }
        } catch (Exception e) {
            System.err.println("Failed to clean up database: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    System.err.println("Failed to close connection: " + e.getMessage());
                }
            }
        }
    }

    @AfterAll
    static void tearDown2() throws Exception {
        try (Connection connection = DriverManager.getConnection(DatabaseConnector.JDBC_URL, DatabaseConnector.USER, DatabaseConnector.PASSWORD);
             Statement statement = connection.createStatement()) {

            // Delete associated records from SoldBookType
            statement.execute("DELETE FROM SoldBookType WHERE ISBN = '0000000000001'");

            // Delete associated records from Bill
            statement.execute("DELETE FROM Bill WHERE orderId IN (SELECT orderId FROM SoldBookType WHERE ISBN = '0000000000001')");

            // Delete the test books
            statement.execute("DELETE FROM Book WHERE ISBN = '0000000000001'");
        }
    }

}

