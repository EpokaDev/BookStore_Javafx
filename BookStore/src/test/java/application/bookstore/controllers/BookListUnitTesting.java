package application.bookstore.controllers;

import application.bookstore.models.Book;
import javafx.scene.image.Image;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import javafx.application.Platform;

public class BookListUnitTesting {

    private BookList bookList;

    @Mock
    private Connection mockConnection;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeAll
    static void setupJavaFX() {
        if (!Platform.isFxApplicationThread()) {
            try {
                Platform.startup(() -> {
                });
            } catch (IllegalStateException e) {
                // Ignore if already started
            }
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        bookList = new BookList();

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery("SELECT * FROM Book")).thenReturn(mockResultSet);
    }

    @Test
    public void testGetBooks() throws Exception {
        // Mock the Connection, Statement, and ResultSet
        Connection mockConnection = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Configure the mock behavior
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery("SELECT * FROM Book")).thenReturn(mockResultSet);

        // Mock the ResultSet to simulate two books
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("ISBN")).thenReturn("12345", "67890");
        when(mockResultSet.getString("name")).thenReturn("Book1", "Book2");
        when(mockResultSet.getString("author")).thenReturn("Author1", "Author2");
        when(mockResultSet.getString("category")).thenReturn("Fiction", "Non-Fiction");
        when(mockResultSet.getInt("supplier")).thenReturn(1, 2);
        when(mockResultSet.getString("description")).thenReturn("Desc1", "Desc2");
        when(mockResultSet.getString("bookURL")).thenReturn(
                "file:/Users/regiloshi/IdeaProjects/BookStore_JavafxTesting/BookStore/Images/group-3.png",
                "file:/Users/regiloshi/IdeaProjects/BookStore_JavafxTesting/BookStore/Images/group-3.png"
        );
        when(mockResultSet.getDouble("original_price")).thenReturn(10.0, 15.0);
        when(mockResultSet.getDouble("selling_price")).thenReturn(12.0, 18.0);
        when(mockResultSet.getInt("quantity")).thenReturn(3, 6);

        ArrayList<Book> books = bookList.getBooks(mockConnection);

        assertEquals(2, books.size(), "Expected two books but found: " + books.size());
        assertEquals("12345", books.get(0).getISBN());
        assertEquals("67890", books.get(1).getISBN());
        assertEquals(1, BookList.booksWithLowQuantity.size(),
                "Expected one book with low quantity but found: " + BookList.booksWithLowQuantity.size());
        assertEquals("Book1", BookList.booksWithLowQuantity.get(0).getTitle());
    }




    @Test
    public void testGetCategories() {
        bookList.getCategories().add("Fiction");
        bookList.getCategories().add("Non-Fiction");

        ArrayList<String> categories = bookList.getCategories();

        assertNotNull(categories);
        assertTrue(categories.contains("Fiction"));
        assertTrue(categories.contains("Non-Fiction"));
    }
}
