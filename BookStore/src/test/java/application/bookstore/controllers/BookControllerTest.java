package application.bookstore.controllers;

import application.bookstore.models.Book;
import application.bookstore.models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private User testUser;
    private Book testBook;
    private ObservableList<Book> bookList;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;

    static {
        System.setProperty("java.awt.headless", "true");
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
    }

    @BeforeEach
    void setUp() throws SQLException {
        testUser = createTestUser();
        testBook = createTestBook();
        bookList = FXCollections.observableArrayList();

        setupMockDatabaseComponents();
    }

    private User createTestUser() {
        return new User(
                new SimpleStringProperty("John"),
                new SimpleStringProperty("Doe"),
                new SimpleStringProperty("john.doe@example.com"),
                new SimpleStringProperty("johndoe"),
                new SimpleStringProperty("password123"),
                new SimpleStringProperty("Male"),
                new SimpleStringProperty("Librarian")
        );
    }

    private Book createTestBook() {
        return new Book(
                "1234567890",
                "Test Book",
                "Test Author",
                "Test Category",
                1,
                "Test Description",
                null,
                10.00,
                15.00,
                100
        );
    }

    private void setupMockDatabaseComponents() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt(1)).thenReturn(1);
        doNothing().when(mockPreparedStatement).setString(anyInt(), anyString());
        doNothing().when(mockPreparedStatement).setDouble(anyInt(), anyDouble());
        doNothing().when(mockPreparedStatement).setInt(anyInt(), anyInt());
    }

    private void setupMockDatabase(Runnable testLogic) throws SQLException {
        try (MockedStatic<DriverManager> mockedStatic = mockStatic(DriverManager.class)) {
            mockedStatic.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);
            testLogic.run();
        }
    }

    @DisplayName("Boundary Analysis Testing for amount in generate Bill")
    @ParameterizedTest
    @CsvSource({
            "0.01", // min
            "0.02", // min+
            "5000", // normal
            "9999.98", // max-
            "9999.99" //max+

    })
    void boundaryTestingValidAmount(double minAmount) throws SQLException {
        setupMockDatabase(() -> {
            testBook.setChosenQuantity(1);
            bookList.add(testBook);

            assertDoesNotThrow(() -> {
                BookController.generateBillToDatabase(bookList, minAmount, testUser);
            });
        });
    }

    @DisplayName("Boundary Analysis Testing for book quantity in generate Bill")
    @ParameterizedTest
    @CsvSource({
            "1", // min
            "2", // min+
            "50", // normal
            "98", // max-
            "99" //max+

    })
    void boundaryTestingBookQuantity(int bookAmount) throws SQLException {
        setupMockDatabase(() -> {
            testBook.setChosenQuantity(bookAmount);
            bookList.add(testBook);
            double validAmount = 15.00;

            assertDoesNotThrow(() -> {
                BookController.generateBillToDatabase(bookList, validAmount, testUser);
            });
        });
    }





}