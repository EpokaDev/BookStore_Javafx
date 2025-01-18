package application.bookstore.controllers;

import application.bookstore.models.Book;
import application.bookstore.models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static application.bookstore.controllers.BookController.generateBillToDatabase;
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
    private double amount;
    private ResultSet mockGeneratedKeys;

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

        mockGeneratedKeys = mock(ResultSet.class);



        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);

        when(mockGeneratedKeys.next()).thenReturn(true, false);
        when(mockGeneratedKeys.getInt(1)).thenReturn(1);

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

//    // !!!! Boundary Analysis Testing, values chosen arbitrary since no real defined limit !!!!
    @MockitoSettings(strictness = Strictness.LENIENT)
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

    @MockitoSettings(strictness = Strictness.LENIENT)
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
    @MockitoSettings(strictness = Strictness.LENIENT)
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
    @MockitoSettings(strictness = Strictness.LENIENT)
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

// CODE COVERAGE TESTING METHOD 1://

    //STATEMENT, BRANCH, CONDITION TESTING
    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void testGenerateBillToDatabase_SuccessfulInsertion() throws SQLException {
        bookList.add(testBook);
        setupMockDatabase(() -> {
            assertDoesNotThrow(() -> generateBillToDatabase(bookList, amount, testUser));
        });

        verify(mockPreparedStatement, times(2)).executeUpdate();
        verify(mockPreparedStatement).setObject(eq(1), any(LocalDateTime.class));
        verify(mockPreparedStatement).setString(2, testUser.getUsername());
        verify(mockPreparedStatement).setDouble(3, amount);
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void testGenerateBillToDatabase_NegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> generateBillToDatabase(bookList, -5.0, testUser));
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void testGenerateBillToDatabase_EmptyBookList() throws SQLException {
        setupMockDatabase(() -> {
            assertDoesNotThrow(() -> generateBillToDatabase(bookList, 0.0, testUser));
        });

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void testGenerateBillToDatabase_FailedToRetrieveKeys() throws SQLException {
        bookList.add(testBook);

        setupMockDatabase(() -> {
            try {
                when(mockGeneratedKeys.next()).thenReturn(false);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> generateBillToDatabase(bookList, amount, testUser));

            assertTrue(thrown.getCause() instanceof SQLException);
        });

        verify(mockPreparedStatement).getGeneratedKeys();
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void testGenerateBillToDatabase_SecondStatementFailure() throws SQLException {
        bookList.add(testBook);

        PreparedStatement mockFailingStatement = mock(PreparedStatement.class);
        when(mockFailingStatement.executeUpdate()).thenThrow(new SQLException("Second statement failed"));

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockFailingStatement);

        setupMockDatabase(() -> {
            assertThrows(RuntimeException.class,
                    () -> generateBillToDatabase(bookList, amount, testUser));
        });
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void testGenerateBillToDatabase_ExecuteUpdateSQLException() throws SQLException {
        bookList.add(testBook);

        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("SQL error during executeUpdate"));

        setupMockDatabase(() -> {
            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> generateBillToDatabase(bookList, amount, testUser));

            assertTrue(thrown.getCause() instanceof SQLException);
            assertEquals("SQL error during executeUpdate", thrown.getCause().getMessage());
        });

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void testGenerateBillToDatabase_SQLExceptionInLoop() throws SQLException {
        bookList.add(testBook);


        when(mockPreparedStatement.executeUpdate())
                .thenReturn(1)
                .thenThrow(new SQLException("SQL error during book insertion"));

        setupMockDatabase(() -> {
            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> generateBillToDatabase(bookList, amount, testUser));

            assertTrue(thrown.getCause() instanceof SQLException);
        });

        verify(mockPreparedStatement, times(2)).executeUpdate();
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void testGenerateBillToDatabase_SQLExceptionDuringPrepareStatement() throws SQLException {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenThrow(new SQLException("Error preparing statement"));

        setupMockDatabase(() -> {
            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> generateBillToDatabase(bookList, amount, testUser));

            assertTrue(thrown.getCause() instanceof SQLException);
        });

        verify(mockPreparedStatement, never()).executeUpdate();
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void testGenerateBillToDatabaseConnectionFailure() {
        try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(
                            "test", "test", "test"))
                    .thenThrow(new SQLException("Connection failed"));

            assertThrows(RuntimeException.class, () -> BookController.generateBillToDatabase(bookList, 100.0, testUser));
        }
    }


    /* !!!!!!!!!! MC-DC !!!!!!!!!!!*/
    // Same test as above
    @Tag("MC-DC")
    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void testFalseAmountPositiveKeyMCDC() {
        assertThrows(IllegalArgumentException.class,
                () -> BookController.generateBillToDatabase(bookList, -5.0, testUser));
    }

    @Tag("MC-DC")
    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    public void testBothConditionsTrueMCDC() throws SQLException {
        setupMockDatabase(() -> {
            ObservableList<Book> books = FXCollections.observableArrayList(
                    testBook
            );
            assertDoesNotThrow(() -> generateBillToDatabase(books, 15.0, testUser));
        });
    }

    @Tag("MC-DC")
    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void testTrueAmountFalseKeyMCDC() throws SQLException {
        bookList.add(testBook);

        setupMockDatabase(() -> {
            try {
                when(mockGeneratedKeys.next()).thenReturn(false);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> generateBillToDatabase(bookList, amount, testUser));

            assertTrue(thrown.getCause() instanceof SQLException);
        });

        verify(mockPreparedStatement).getGeneratedKeys();
    }


//    CODE COVERAGE TESTING: METHOD 1://
//
//    STATEMENT, BRANCH, CONDITION TESTING
    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    @DisplayName("Generate Bill - Normal Path With Single Book")
    void generateBillNormalPathSingleBook() throws SQLException {

        ObservableList<Book> selectedBooks = FXCollections.observableArrayList();
        selectedBooks.add(testBook);
        double amount = 15.00;


        File billsFolder = new File("bills");
        if (!billsFolder.exists()) {
            billsFolder.mkdir();
        }


        setupMockDatabase(() -> {
            assertDoesNotThrow(() -> {
                BookController.generateBill(testUser, selectedBooks, amount);
            });

            File[] files = billsFolder.listFiles((dir, name) -> name.startsWith("bill_"));
            assertNotNull(files);
            assertTrue(files.length > 0);

            for (File file : files) {
                file.delete();
            }
        });

        billsFolder.delete();
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    @DisplayName("Generate Bill - Normal Path With Multiple Books")
    void generateBillNormalPathMultipleBooks() throws SQLException {

        ObservableList<Book> selectedBooks = FXCollections.observableArrayList();
        selectedBooks.addAll(bookList);
        double amount = 40.00;


        File billsFolder = new File("bills");
        if (!billsFolder.exists()) {
            billsFolder.mkdir();
        }


        setupMockDatabase(() -> {

            assertDoesNotThrow(() -> {
                BookController.generateBill(testUser, selectedBooks, amount);
            });

            File[] files = billsFolder.listFiles((dir, name) -> name.startsWith("bill_"));
            assertNotNull(files);
            assertTrue(files.length > 0);

            for (File file : files) {
                file.delete();
            }
        });

        billsFolder.delete();
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    @DisplayName("Generate Bill - IOException Handling")
    void generateBillIOException() throws SQLException {

        bookList.add(testBook);
        double amount = 15.00;

        File billsFolder = new File("bills");
        if (!billsFolder.exists()) {
            billsFolder.mkdir();
        }
        billsFolder.setReadOnly();

        setupMockDatabase(() -> {
            assertDoesNotThrow(() -> {
                BookController.generateBill(testUser, bookList, amount);
            });
        });


        billsFolder.setWritable(true);
        billsFolder.delete();
    }


    // !!!!!!!!!! MC-DC !!!!!!!!!!
    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    @DisplayName("MC-DC2->ALL CONDITIONS TRUE")
    void allConditionsTrue() throws SQLException {

        ObservableList<Book> selectedBooks = FXCollections.observableArrayList();
        selectedBooks.add(testBook);
        double amount = 15.00;


        File billsFolder = new File("bills");
        if (!billsFolder.exists()) {
            billsFolder.mkdir();
        }


        setupMockDatabase(() -> {
            assertDoesNotThrow(() -> {
                BookController.generateBill(testUser, selectedBooks, amount);
            });

            File[] files = billsFolder.listFiles((dir, name) -> name.startsWith("bill_"));
            assertNotNull(files);
            assertTrue(files.length > 0);

            for (File file : files) {
                file.delete();
            }
        });

        billsFolder.delete();
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    @DisplayName("MC-DC 2 -> Directory Non-Existence Handling")
    void generateBillCreatesDirectory() throws SQLException {
        File billsFolder = new File("bills");
        if (billsFolder.exists()) {
            deleteDirectory(billsFolder);
        }

        ObservableList<Book> selectedBooks = FXCollections.observableArrayList();
        selectedBooks.add(testBook);
        double amount = 15.00;

        setupMockDatabase(() -> {
            assertThrows(RuntimeException.class, () -> {
                BookController.generateBill(testUser, selectedBooks, amount);
            });
        });

        deleteDirectory(billsFolder);
    }

    // Helper function to delete directory and contents
    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    @DisplayName("MC-DC 2 - IOException FALSE")
    void falseBillIOExceptionMCDC() throws SQLException {

        bookList.add(testBook);
        double amount = 15.00;

        File billsFolder = new File("bills");
        if (!billsFolder.exists()) {
            billsFolder.mkdir();
        }
        billsFolder.setReadOnly();

        setupMockDatabase(() -> {
            assertDoesNotThrow(() -> {
                BookController.generateBill(testUser, bookList, amount);
            });
        });


        billsFolder.setWritable(true);
        billsFolder.delete();
    }
}