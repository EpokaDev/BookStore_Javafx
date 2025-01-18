package application.bookstore.controllers;

import application.bookstore.models.Book;
import application.bookstore.models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookControllerIntegrationTest {

    /*  1)The interaction between multiple components in the system::
        BookController.generateBillToDatabase
        Database components (Connection, PreparedStatement, ResultSet)
        Data models (Book, User)
        2)Integration Points:
        The test validates:
        How generateBillToDatabase interacts with the database to insert a new bill (Bill table).
        How it interacts to insert book details into SoldBookType table.
        Proper use of generated keys from the database.
     */

    @Test
    void testBillGenerationIntegration_ValidInput() throws SQLException {
        ObservableList<Book> books = FXCollections.observableArrayList(
                new Book("1234567890", "Integration Book", "Author", "Category", 1,
                        "Description", null, 10.0, 15.0, 10)
        );
        StringProperty firstName = new SimpleStringProperty("John");
        StringProperty lastName = new SimpleStringProperty("Doe");
        StringProperty email = new SimpleStringProperty("john.doe@example.com");
        StringProperty username = new SimpleStringProperty("johndoe");
        StringProperty password = new SimpleStringProperty("password123");
        StringProperty gender = new SimpleStringProperty("Male");
        StringProperty role = new SimpleStringProperty("Librarian");

        User user = new User(firstName, lastName, email, username, password, gender, role);

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockBillStatement = mock(PreparedStatement.class);
        PreparedStatement mockSoldBookStatement = mock(PreparedStatement.class);
        ResultSet mockGeneratedKeys = mock(ResultSet.class);

        // Mock ResultSet behavior
        when(mockGeneratedKeys.next()).thenReturn(true);
        when(mockGeneratedKeys.getInt(1)).thenReturn(1); // Return a mock order ID

        // Mock PreparedStatement behavior
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockBillStatement); // For Bill insertion
        when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockSoldBookStatement); // For SoldBookType insertion

        when(mockBillStatement.executeUpdate()).thenReturn(1); // Simulate successful update for Bill
        when(mockBillStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys); // Return mock keys

        when(mockSoldBookStatement.executeUpdate()).thenReturn(1); // Simulate successful update for SoldBookType

        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class)) {
            driverManagerMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            // Act
            assertDoesNotThrow(() -> BookController.generateBillToDatabase(books, 15.0, user));

            // Assert
            verify(mockBillStatement, times(1)).executeUpdate(); // Verify Bill insertion
            verify(mockSoldBookStatement, times(1)).executeUpdate(); // Verify SoldBookType insertion
            verify(mockGeneratedKeys, times(1)).getInt(1); // Verify retrieval of generated order ID
        }
    }



    @Test
    void testBillGenerationIntegration_DatabaseFailure() throws SQLException {
        // Arrange
        ObservableList<Book> books = FXCollections.observableArrayList(
                new Book("1234567890", "Integration Book", "Author", "Category", 1,
                        "Description", null, 10.0, 15.0, 10)
        );
        StringProperty firstName = new SimpleStringProperty("John");
        StringProperty lastName = new SimpleStringProperty("Doe");
        StringProperty email = new SimpleStringProperty("john.doe@example.com");
        StringProperty username = new SimpleStringProperty("johndoe");
        StringProperty password = new SimpleStringProperty("password123");
        StringProperty gender = new SimpleStringProperty("Male");
        StringProperty role = new SimpleStringProperty("Admin");

        User user = new User(firstName, lastName, email, username, password, gender, role);

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenThrow(new SQLException("Database error"));

        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class)) {
            driverManagerMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            // Act & Assert
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                BookController.generateBillToDatabase(books, 15.0, user);
            });
            assertEquals("java.sql.SQLException: Database error", thrown.getCause().toString());
        }
    }
}

