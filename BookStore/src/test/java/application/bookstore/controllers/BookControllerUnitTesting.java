package application.bookstore.controllers;

import application.bookstore.models.Book;
import application.bookstore.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.sql.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookControllerUnitTesting {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }


    @Test
    void testGenerateBill_ValidInput() throws IOException {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("Manager");

        Book book1 = new Book("12345", "Book One", "Author One", "Fiction", "A great book", 10.0, 15.0, 5);
        Book book2 = new Book("67890", "Book Two", "Author Two", "Non-Fiction", "Another great book", 12.0, 18.0, 3);
        ObservableList<Book> books = FXCollections.observableArrayList(book1, book2);

        File billsFolder = new File("bills");
        if (!billsFolder.exists()) {
            billsFolder.mkdirs();
        }

        BookController.generateBill(user, books, 45.0);

        File[] files = billsFolder.listFiles((dir, name) -> name.startsWith("bill_") && name.endsWith(".txt"));
        assertNotNull(files);
        assertTrue(files.length > 0);
    }

    @Test
    void testUpdateQuantity_ValidInput() throws SQLException {
        Book book1 = new Book("12345", "Book One", "Author One", "Fiction", "A great book", 10.0, 15.0, 10);
        book1.setChosenQuantity(5);
        ObservableList<Book> books = FXCollections.observableArrayList(book1);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        BookController.updateQuantity(books, mockConnection);

        verify(mockPreparedStatement, times(1)).setInt(1, 5);
        verify(mockPreparedStatement, times(1)).setString(2, "12345");
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGenerateBillToDatabase_InvalidAmount() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");

        ObservableList<Book> books = FXCollections.observableArrayList();

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                BookController.generateBillToDatabase(books, -10.0, user)
        );

        assertEquals("Amount cannot be negative", exception.getMessage());
    }

    @Test
    void testDeleteBook_ValidISBN() throws SQLException {
        String isbn = "12345";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        BookController.deleteBook(isbn, mockConnection);

        verify(mockPreparedStatement, times(1)).setString(1, isbn);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteBook_InvalidISBN() throws SQLException {
        String isbn = "InvalidISBN";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doThrow(new SQLException("No such book")).when(mockPreparedStatement).executeUpdate();

        assertDoesNotThrow(() -> BookController.deleteBook(isbn, mockConnection));
    }

    @Test
    void testDeleteBook_SQLException() throws SQLException {
        String isbn = "12345";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doThrow(new SQLException("Database error")).when(mockPreparedStatement).executeUpdate();

        BookController.deleteBook(isbn, mockConnection);

        verify(mockPreparedStatement, times(1)).setString(1, isbn);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteBookWithInternalConnection_ValidISBN() throws SQLException {
        String isbn = "12345";

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            BookController.deleteBook(isbn);

            verify(mockPreparedStatement, times(1)).setString(1, isbn);
            verify(mockPreparedStatement, times(1)).executeUpdate();
            verify(mockPreparedStatement, times(1)).close();
            verify(mockConnection, times(1)).close();
        }
    }

//    @Test
//    void testDeleteBookWithInternalConnection_SQLException() {
//        String isbn = "12345";
//
//        Connection mockConnection = mock(Connection.class);
//        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
//
//        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
//            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
//                    .thenReturn(mockConnection);
//
//            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
//
//            assertDoesNotThrow(() -> BookController.deleteBook(isbn));
//
//            verify(mockConnection, times(1)).close();
//        } catch (SQLException e) {
//            fail("Unexpected SQLException: " + e.getMessage());
//        }
//    }



}
