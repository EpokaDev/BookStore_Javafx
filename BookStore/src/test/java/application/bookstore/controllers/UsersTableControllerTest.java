package application.bookstore.controllers;

import application.bookstore.models.User;
import application.bookstore.views.UsersTableView;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * In this class I have:
 *   1. Boundary Value Testing (BVT) for removeRow(int row).
 *   2. Class Evaluation / Decision Table Testing for add(User).
 *   3. Code Coverage Testing (Statement, Branch, Condition, MC/DC) for removeRow.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UsersTableControllerTest {
    private static boolean javafxInitialized = false;
    private UsersTableController usersTableController;
    private ObservableList<User> users;
    // Mocked DB components
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;

    @BeforeAll
    static void initJavaFX() {
        if (!javafxInitialized) {
            Platform.startup(() -> {});
            javafxInitialized = true;
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Real JavaFX controls
        TableView<User> tableView = new TableView<>();
        TableColumn<User, String> firstNameCol = new TableColumn<>("First Name");
        TableColumn<User, String> lastNameCol = new TableColumn<>("Last Name");
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        TableColumn<User, String> userNameCol = new TableColumn<>("User Name");
        TableColumn<User, String> passwordCol = new TableColumn<>("Password");
        TableColumn<User, String> genderCol = new TableColumn<>("Gender");
        TableColumn<User, String> roleCol = new TableColumn<>("Role");

        Button addButton = new Button("Add");
        Button removeButton = new Button("Remove");

        users = FXCollections.observableArrayList();

        UsersTableView usersTableView = new UsersTableView(
                tableView,
                firstNameCol,
                lastNameCol,
                emailCol,
                userNameCol,
                passwordCol,
                genderCol,
                roleCol,
                addButton,
                removeButton,
                users
        );

        // This sets items to the real TableView
        tableView.setItems(users);

        setupMockDatabaseComponents();

        usersTableController = new UsersTableController(usersTableView, users);
    }

    private void setupMockDatabaseComponents() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        // By default, DB calls succeed
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    }

    private void setupMockDatabase(Runnable testLogic) throws SQLException {
        try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);
            testLogic.run();
        }
    }

    private User createUser(String username) {
        return new User(
                new SimpleStringProperty("John"),
                new SimpleStringProperty("Doe"),
                new SimpleStringProperty("john.doe@example.com"),
                new SimpleStringProperty(username),
                new SimpleStringProperty("pass123"),
                new SimpleStringProperty("Male"),
                new SimpleStringProperty("Admin")
        );
    }

    // ----------------------------------------------------------------------
    // (a) BOUNDARY VALUE TESTING (BVT) for removeRow(int row)
    // I have chosen removeRow because it has a numeric index 'row'.
    // ----------------------------------------------------------------------

    /**
     * Here I have boundary tests with possible row indices:
     * -1 (below range), 0 (lower boundary), 1 (middle), 2 (size boundary), 5 (beyond size).
     * I have also tested the effect on users list and DB calls.
     */
    @ParameterizedTest
    @CsvSource({
            "-1",
            "0",
            "1",
            "2",
            "5"
    })
    @DisplayName("(a) BVT -> removeRow(index) test with boundary-like values")
    void removeRowBoundaryValueTesting(int index) throws SQLException {
        // I have added some users so that size() = 2
        users.add(createUser("A"));
        users.add(createUser("B"));

        setupMockDatabase(() -> {
            // I have called removeRow for the given index
            usersTableController.removeRow(index);

            if (index >= 0 && index < users.size() + 1) {
                // If index is valid (0 or 1), removal from DB + list
                // After removing from the list, size is now 1
                // But watch out: originally 2 items, so if index=2 or bigger, no removal
                // We'll just check the final size
                if (index == 0 || index == 1) {
                    // valid remove
                    assertEquals(1, users.size(), "One item removed from list");
                    // DB delete called once
                    try {
                        verify(mockPreparedStatement, times(1)).executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // index=2 or 5 is invalid => no removal
                    assertEquals(2, users.size(), "No removal");
                    try {
                        verify(mockPreparedStatement, never()).executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                // index<0 => no removal
                assertEquals(2, users.size(), "No removal for negative index");
                try {
                    verify(mockPreparedStatement, never()).executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

// (b) EQUIVALENCE CLASS TESTING for add(User user).
// I will demonstrate: weak normal, strong normal, weak robust, strong robust.
//
// Dimensions (2):
//   1) username -> valid: "userA", "userB" | invalid: "", "   "
//   2) role     -> valid: "Admin", "Moderator" | invalid: "", "nullVal" (which we parse as null)
// ----------------------------------------------------------------------

    private User createUser(String username, String role) {
        // If the CSV has "nullVal", we'll interpret that as an actual null for role
        if ("nullVal".equals(role)) {
            role = null;
        }
        return new User(
                new SimpleStringProperty("John"),
                new SimpleStringProperty("Doe"),
                new SimpleStringProperty("john.doe@example.com"),
                new SimpleStringProperty(username),
                new SimpleStringProperty("pass123"),
                new SimpleStringProperty("Male"),
                new SimpleStringProperty(role == null ? "Admin" : role)
        );
    }

    /**
     * (1) WEAK NORMAL
     * We test only each dimension's normal partitions separately,
     * while keeping the other dimension also in a normal partition.
     *
     * That means we do NOT combine multiple normal partitions across both dims,
     * we simply test each dimension's normal partition in isolation.
     */
    @ParameterizedTest
    @CsvSource({
            "userA, Admin",    // username dimension normal, role dimension also normal
            // if we had multiple normal subsets for each, we might do separate lines
            // for each dimension individually, but let's keep it minimal for demonstration.
    })
    @DisplayName("(b1) Weak Normal Equivalence -> add(User) [username & role both 'normal']")
    void weakNormalAddUserTest(String username, String role) throws SQLException {
        // We consider only normal partition for each dimension individually here.
        // But in practice, "weak normal" is often just 1 line per dimension tested in isolation.

        User user = createUser(username, role);

        setupMockDatabase(() -> {
            assertDoesNotThrow(() -> usersTableController.add(user));
            assertTrue(users.contains(user));
            try {
                verify(mockPreparedStatement, atLeastOnce()).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * (2) STRONG NORMAL
     * We test all possible combinations of normal partitions for all dimensions.
     * Suppose each dimension has 2 normal partitions. That yields a cross-product.
     *
     * Dimension A (username) normal: "userA", "userB"
     * Dimension B (role)     normal: "Admin", "Moderator"
     * => total combos: 2 x 2 = 4 lines in the CSV.
     */
    @ParameterizedTest
    @CsvSource({
            "userA, Admin",
            "userA, Moderator",
            "userB, Admin",
            "userB, Moderator"
    })
    @DisplayName("(b2) Strong Normal Equivalence -> add(User) [all normal combos of username & role]")
    void strongNormalAddUserTest(String username, String role) throws SQLException {
        // This ensures we try all normal combos for both dims.
        User user = createUser(username, role);

        setupMockDatabase(() -> {
            assertDoesNotThrow(() -> usersTableController.add(user));
            assertTrue(users.contains(user));
            try {
                verify(mockPreparedStatement, atLeastOnce()).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * (3) WEAK ROBUST
     * We test one dimension's invalid partition at a time,
     * while keeping the other dimension in its normal partition.
     *
     * So we vary username invalid while role is normal,
     * or vary role invalid while username is normal,
     * but we don't combine invalid partitions from both dimensions simultaneously.
     */
    @ParameterizedTest
    @CsvSource({
            // Invalid username, role normal
            "'', Admin",
            "'   ', Admin",
            // Invalid role, username normal
            "userA, ''",
            "userA, nullVal"
    })
    @DisplayName("(b3) Weak Robust Equivalence -> add(User) [one dimension invalid, other normal]")
    void weakRobustAddUserTest(String username, String role) throws SQLException {
        // If role is "nullVal", we treat it as null in createUser(...)
        User user = createUser(username, role);

        setupMockDatabase(() -> {
            // Typically I'd check that the code either logs an error
            // or inserts anyway. We assume it won't throw outward exceptions.
            assertDoesNotThrow(() -> usersTableController.add(user));
            // The user might end up in the list no matter what.
            assertTrue(users.contains(user));
            // Possibly check if DB insertion occurred
            try {
                verify(mockPreparedStatement, atLeastOnce()).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * (4) STRONG ROBUST
     * We test the cross-product of all partitions (valid + invalid) across all dimensions.
     *
     * For username: { "userA", "userB" } [valid], { "", "   " } [invalid]
     * For role:     { "Admin", "Moderator" } [valid], { "", "nullVal" } [invalid]
     * => 4 x 4 = 16 combos total. I'll just show a few lines for demonstration.
     */
    @ParameterizedTest
    @CsvSource({
            // username valid, role valid
            "userA, Admin",
            "userA, Moderator",
            "userB, Admin",
            "userB, Moderator",
            // username valid, role invalid
            "userA, ''",
            "userA, nullVal",
            "userB, ''",
            "userB, nullVal",
            // username invalid, role valid
            "'', Admin",
            "'   ', Admin",
            "'', Moderator",
            "'   ', Moderator",
            // username invalid, role invalid
            "'', ''",
            "'', nullVal",
            "'   ', ''",
            "'   ', nullVal"
    })
    @DisplayName("(b4) Strong Robust Equivalence -> add(User) [cross-product of all valid+invalid in both dims]")
    void strongRobustAddUserTest(String username, String role) throws SQLException {
        User user = createUser(username, role);

        setupMockDatabase(() -> {
            // This covers all combos, including multiple dims invalid at once
            assertDoesNotThrow(() -> usersTableController.add(user));
            assertTrue(users.contains(user));
            // I'd check that DB insertion is attempted in all cases,
            // or see if we handle certain combos differently
            try {
                verify(mockPreparedStatement, atLeastOnce()).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ----------------------------------------------------------------------
    // (c) CODE COVERAGE TESTING for removeRow(...) -> Statement, Branch,
    // Condition, MC/DC coverage.
    // ----------------------------------------------------------------------

    /**
     * I have statement coverage by calling removeRow in both valid/invalid scenarios.
     * I have branch coverage by ensuring we test row in valid range vs. invalid range.
     * I have condition coverage for (row >= 0 && row < size).
     * I have MC/DC by toggling each condition:
     * - row >= 0 true/false
     * - row < size true/false
     */
    @Tag("Coverage")
    @Test
    @DisplayName("(c)(i-iv) removeRow -> Code Coverage Test (includes MC/DC toggles)")
    void testRemoveRowCoverage() throws SQLException {
        // I have user list with size=2
        users.add(createUser("Alpha"));
        users.add(createUser("Beta"));

        setupMockDatabase(() -> {
            // 1) row = -1 => row >= 0 = false => no removal
            usersTableController.removeRow(-1);
            assertEquals(2, users.size());

            // 2) row = 0 => row >= 0 = true && row < 2 => true => valid
            usersTableController.removeRow(0);
            assertEquals(1, users.size(), "Removed one user at index 0");
            // DB call 1 time so far
            try {
                verify(mockPreparedStatement, times(1)).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // 3) row = 5 => row >= 0 = true but row < 1? => false => no removal
            usersTableController.removeRow(5);
            assertEquals(1, users.size(), "No removal for out-of-range index");
            try {
                // Still 1 total call
                verify(mockPreparedStatement, times(1)).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
