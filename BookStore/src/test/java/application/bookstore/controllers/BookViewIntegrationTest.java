package application.bookstore.controllers;

import application.bookstore.controllers.BookController;
import application.bookstore.controllers.BookList;
import application.bookstore.models.Book;
import application.bookstore.models.User;
import application.bookstore.views.AddBookView;
import application.bookstore.views.BookView;
import application.bookstore.views.EditBookView;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookViewIntegrationTest extends ApplicationTest {

//    private BookView bookView;
//    private BookList mockBookList;
//    private BookController mockBookController;
//    private ObservableList<Book> books;
//    private User user;
//
//    @BeforeEach
//    void setup() {
//        user = new User(
//                new SimpleStringProperty("John"),
//                new SimpleStringProperty("Doe"),
//                new SimpleStringProperty("john.doe@example.com"),
//                new SimpleStringProperty("johndoe"),
//                new SimpleStringProperty("password123"),
//                new SimpleStringProperty("Male"),
//                new SimpleStringProperty("Librarian")
//        );
//    }
//
//
//    @BeforeAll
//    static void setupJavaFX() {
//        if (!Platform.isFxApplicationThread()) {
//            try {
//                Platform.startup(() -> {});
//            } catch (IllegalStateException e) {
//                // Ignore if already started
//            }
//        }
//    }
//
//
//
//    @Override
//    public void start(Stage stage) {
//        // Mock BookList and BookController
//        mockBookList = mock(BookList.class);
//        mockBookController = mock(BookController.class);
//
//        // Mock Book data
//        books = FXCollections.observableArrayList(
//                new Book("1234567890", "Test Book 1", "Author 1", "Category 1", 1,
//                        "Description 1", null, 10.0, 15.0, 5),
//                new Book("0987654321", "Test Book 2", "Author 2", "Category 2", 1,
//                        "Description 2", null, 12.0, 18.0, 10)
//        );
//        when(mockBookList.getBooks()).thenReturn(new ArrayList<>(books));
//
//
//        // Create User
//        User user = new User(
//                new SimpleStringProperty("John"),
//                new SimpleStringProperty("Doe"),
//                new SimpleStringProperty("john.doe@example.com"),
//                new SimpleStringProperty("johndoe"),
//                new SimpleStringProperty("password123"),
//                new SimpleStringProperty("Male"),
//                new SimpleStringProperty("Librarian")
//        );
//
//        // Initialize BookView with mocks
//        bookView = new BookView(new SimpleStringProperty("Librarian"), user);
//        stage.setScene(bookView.showView(stage));
//        stage.show();
//    }
//
//    @Test
//    void testFilterBooksByCategory() {
//        Platform.runLater(() -> {
//            // Populate books with unique entries
//            books.clear(); // Clear any existing data
//            books.addAll(
//                    new Book("1234567890", "Test Book 1", "Author 1", "Category 1", 1, "Description 1", null, 10.0, 15.0, 5),
//                    new Book("0987654321", "Test Book 2", "Author 2", "Category 2", 1, "Description 2", null, 12.0, 18.0, 10)
//            );
//
//            // Sync BookView's books list with the test data
//            bookView.books = new ArrayList<>(books);
//
//            // Set initial items in the TableView
//            bookView.getTableView().setItems(FXCollections.observableArrayList(bookView.books));
//
//            // Apply the filter
//            bookView.filterTable("Category 1", "");
//
//            // Wait for UI updates
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//
//            // Fetch filtered books
//            ObservableList<Book> filteredBooks = bookView.getTableView().getItems();
//
//            // Debugging: Log filtered books
//            System.out.println("Filtered Books:");
//            filteredBooks.forEach(book -> System.out.println(book.getTitle()));
//
//            // Verify filtered results
//            assertEquals(1, filteredBooks.size(), "Expected one book in 'Category 1'");
//            assertEquals("Test Book 1", filteredBooks.get(0).getTitle());
//        });
//    }
//
//    @Test
//    void testAddBookButtonOpensAddBookView() {
//        Platform.runLater(() -> {
//            // Mock AddBookView
//            AddBookView mockAddBookView = mock(AddBookView.class);
//
//            // Mock the behavior of showView to return a valid Scene
//            when(mockAddBookView.showView(any(Stage.class))).thenReturn(new Scene(new VBox()));
//
//            // Inject the mock into BookView
//            bookView = new BookView(new SimpleStringProperty("Librarian"), user, mockAddBookView);
//
//            // Simulate clicking the "Add Book" button
//            bookView.addBookButtonClicked();
//
//            // Verify that the mock's showView method was called
//            verify(mockAddBookView, times(1)).showView(any(Stage.class));
//        });
//    }
//
//
//
//    @Test
//    void testEditBookFunctionality() {
//        Platform.runLater(() -> {
//            // Mock EditBookView
//            EditBookView mockEditBookView = mock(EditBookView.class);
//
//            // Mock the behavior of showView
//            when(mockEditBookView.showView(any(Stage.class))).thenReturn(new Scene(new VBox()));
//
//            // Inject the mock into BookView
//            bookView = new BookView(new SimpleStringProperty("Librarian"), user, mockEditBookView);
//
//            // Simulate editing a book
//            Book selectedBook = books.get(0);
//            bookView.editBook(selectedBook);
//
//            // Verify that the mock's showView method was called
//            verify(mockEditBookView, times(1)).showView(any(Stage.class));
//        });
//    }

}

