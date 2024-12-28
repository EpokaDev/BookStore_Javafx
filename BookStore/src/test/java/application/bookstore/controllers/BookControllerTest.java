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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

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

    // !!!! Boundary Analysis Testing, values chosen arbitrary since no real defined limit !!!!

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
            testBook.setChosenQuantity(1); // value stays the same since boundary analysis assumes single fault
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
            double validAmount = 15.00; // value stays at norm since boundary analysis assumes single fault

            assertDoesNotThrow(() -> {
                BookController.generateBillToDatabase(bookList, validAmount, testUser);
            });
        });
    }

    // !!!!!!! Equivalence Class Testing !!!!!!!!

    /* Arbitrary classes chosen

    -- Amount of money

        Set 1: 0.01 - 99.99 (Small amounts)

        Lower boundary: 0.01
        Representative value: 50.00
        Upper boundary: 99.99


        Set 2: 100.00 - 4999.99 (Medium amounts)

        Lower boundary: 100.01
        Representative value: 2500.00
        Upper boundary: 4999.99


        Set 3: 5000.00 - 9999.99 (Large amounts)

        Lower boundary: 5000.01
        Representative value: 7500.00
        Upper boundary: 9999.99

    -- Amount of books

        Set 1: 1 - 50 (Small amount of books)

        Lower boundary: 1
        Representative value: 25
        Upper boundary: 50


        Set 2: 50 - 100 (High amount of books)

        Lower boundary: 50
        Representative value: 75
        Upper boundary: 100
    */


    // !!!!!!!! WEAK EQUIVALENCE NORMAL TESTING !!!!!!!!!!!!
    @ParameterizedTest
    @CsvSource({
            "50.00, 25",  // Small amount of money and books
            "2500.00, 75",// Medium amount of money and high amount of books
            "5000.00, 25" // Large amount of money and small amount of books
    })
    void weakEquivalenceClassTesting(double amount, int quantity) throws SQLException {
        setupMockDatabase(() -> {
            testBook.setChosenQuantity(quantity);
            bookList.add(testBook);

            assertDoesNotThrow(() -> {
                BookController.generateBillToDatabase(bookList, amount, testUser);
            });
        });
    }

    // !!!!!!!! STRONG EQUIVALENCE NORMAL TESTING !!!!!!!!!!!!
    @ParameterizedTest
    @CsvSource({
            // All combinations of small, medium, and large amounts with small and high book quantities
            "50.00, 25",  // Small amount of money and books
            "50.00, 75",  // Small amount of money and high books
            "2500.00, 25",// Medium amount of money and small books
            "2500.00, 75",// Medium amount of money and high books
            "5000.00, 25",// Large amount of money and small books
            "5000.00, 75" // Large amount of money and high books
    })
    void strongEquivalenceClassTesting(double amount, int quantity) throws SQLException {
        setupMockDatabase(() -> {
            testBook.setChosenQuantity(quantity);
            bookList.add(testBook);

            assertDoesNotThrow(() -> {
                BookController.generateBillToDatabase(bookList, amount, testUser);
            });
        });
    }

    /*
     FOR ROBUST EQUIVALENCE TESTING, INVALID CASES WILL BE INCLUDED TOO
     --AMOUNT OF MONEY:
        INVALID SET 1: AMOUNT < 0
     --AMOUNT OF BOOKS:
        INVALID SET 2: BOOK QUANTITY < 0
    */

    @MockitoSettings(strictness = Strictness.LENIENT) // this line is required by java i dont know why
    @ParameterizedTest
    @CsvSource({
            "50.00, 25",
            "2500.00, 75",
            "-1.00, 25",
            "5000.00, -10"
    })
    void weakRobustEquivalenceClassTesting(double amount, int quantity) throws SQLException {
        setupMockDatabase(() -> {
            if (quantity < 0) {
                assertThrows(IllegalArgumentException.class, () -> testBook.setChosenQuantity(quantity));
            } else {
                testBook.setChosenQuantity(quantity);
                bookList.add(testBook);

                if (amount < 0) {
                    assertThrows(IllegalArgumentException.class, () -> {
                        BookController.generateBillToDatabase(bookList, amount, testUser);
                    });
                } else {
                    assertDoesNotThrow(() -> {
                        BookController.generateBillToDatabase(bookList, amount, testUser);
                    });
                }
            }
        });
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @ParameterizedTest
    @CsvSource({
            // Valid combinations
            "50.00, 25",    // Small money & books
            "50.00, 75",    // Small money & high books
            "2500.00, 25",  // Medium money & small books
            "2500.00, 75",  // Medium money & high books
            "5000.00, 25",  // Large money & small books
            "5000.00, 75",  // Large money & high books
            // Invalid combinations
            "-1.00, 25",    // Invalid: Negative money
            "50.00, -10",   // Invalid: Negative books
            "-1.00, -10" // Invalid: Both money & books out of bounds
    })
    void strongRobustEquivalenceClassTesting(double amount, int quantity) throws SQLException {
        setupMockDatabase(() -> {
            if (quantity < 0) {
                assertThrows(IllegalArgumentException.class, () -> testBook.setChosenQuantity(quantity));
            } else {
            testBook.setChosenQuantity(quantity);
            bookList.add(testBook);

            if (amount < 0) {
                assertThrows(IllegalArgumentException.class, () -> {
                    BookController.generateBillToDatabase(bookList, amount, testUser);
                });
            } else {
                assertDoesNotThrow(() -> {
                    BookController.generateBillToDatabase(bookList, amount, testUser);
                });
            }
        }});
    }










}