package unitTesting;

import application.bookstore.controllers.UsersTableController;
import application.bookstore.models.User;
import application.bookstore.views.UsersTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersTableControllerTest {

    @Mock
    private UsersTableView mockView;

    @Mock
    private TableView<User> mockTableView;

    @Mock
    private TableColumn<User, String> mockFirstNameColumn;

    @Mock
    private TableColumn<User, String> mockLastNameColumn;
    // ... similarly for emailColumn, userNameColumn, passwordColumn, etc.

    @Mock
    private Button mockAddButton;

    @Mock
    private Button mockRemoveButton;

    private ObservableList<User> users;
    private UsersTableController controller;

    @BeforeEach
    void setUp() {
        // We'll use a real ObservableList, but a mocked TableView
        this.users = FXCollections.observableArrayList();

        when(mockView.getTableView()).thenReturn(mockTableView);
        when(mockView.getFirstNameColumn()).thenReturn(mockFirstNameColumn);
        when(mockView.getLastNameColumn()).thenReturn(mockLastNameColumn);
        // ... do the same for other columns ...
        when(mockView.getAddButton()).thenReturn(mockAddButton);
        when(mockView.getRemoveButton()).thenReturn(mockRemoveButton);

        // We'll mock tableView.getItems() to return our real 'users' list:
        when(mockTableView.getItems()).thenReturn(users);

        // Create the controller, which will wire up the listeners
        controller = new UsersTableController(mockView, users);
    }

    @Test
    @DisplayName("updateRowInDatabase(...) should run UPDATE query with correct parameters")
    void testUpdateRowInDatabase() throws SQLException {
        // 1. Create a sample user
        User sampleUser = new User(
                new SimpleStringProperty("John"),
                new SimpleStringProperty("Doe"),
                new SimpleStringProperty("john.doe@example.com"),
                new SimpleStringProperty("john123"),
                new SimpleStringProperty("password123"),
                new SimpleStringProperty("Male"),
                new SimpleStringProperty("User")
        );

        // 2. We'll mock the DB calls with DriverManager
        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStatement = mock(PreparedStatement.class);

            // Return our mock connection whenever getConnection() is called
            driverManagerMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

            // 3. Call the method we want to test
            // Let's simulate updating the 'firstName' column.
            controller.updateRowInDatabase(
                    sampleUser,
                    "firstName", // column
                    "Johnny",    // new value
                    "userName",  // condition column
                    "john123"    // condition value
            );

            // 4. Verify that we ran the correct UPDATE statement
            verify(mockConnection).prepareStatement(
                    eq("UPDATE user SET firstName = ? WHERE userName = ?")
            );
            verify(mockStatement).setString(1, "Johnny");
            verify(mockStatement).setString(2, "john123");
            verify(mockStatement).executeUpdate();
        }
    }

    @Test
    @DisplayName("add(User) should add user to list and run INSERT statement")
    void testAddUser() throws SQLException {
        // 1. Create a new user
        User newUser = new User(
                new SimpleStringProperty("Alice"),
                new SimpleStringProperty("Smith"),
                new SimpleStringProperty("alice.smith@example.com"),
                new SimpleStringProperty("alice123"),
                new SimpleStringProperty("pass456"),
                new SimpleStringProperty("Female"),
                new SimpleStringProperty("User")
        );

        // 2. Mock DB calls
        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStatement = mock(PreparedStatement.class);

            driverManagerMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

            // 3. Call add(user)
            controller.add(newUser);

            // 4. Verify user is added to the observable list
            assertTrue(users.contains(newUser), "User should be added to the observable list");

            // 5. Verify DB INSERT
            verify(mockConnection).prepareStatement(eq("INSERT INTO user (firstName, lastName, email, userName, password, gender, Role) VALUES (?,?,?,?,?,?,?);"));
            verify(mockStatement).setString(1, "Alice");
            verify(mockStatement).setString(2, "Smith");
            verify(mockStatement).setString(3, "alice.smith@example.com");
            verify(mockStatement).setString(4, "alice123");
            verify(mockStatement).setString(5, "pass456");
            verify(mockStatement).setString(6, "Female");
            verify(mockStatement).setString(7, "User");
            verify(mockStatement).executeUpdate();
        }
    }

    @Test
    @DisplayName("removeRow(row) should remove user from list and run DELETE statement")
    void testRemoveRow() throws SQLException {
        // 1. Put a user in the list
        User userToRemove = new User(
                new SimpleStringProperty("Bob"),
                new SimpleStringProperty("Builder"),
                new SimpleStringProperty("bob@example.com"),
                new SimpleStringProperty("bob123"),
                new SimpleStringProperty("bobPass"),
                new SimpleStringProperty("Male"),
                new SimpleStringProperty("User")
        );
        users.add(userToRemove);

        // 2. Mock DB calls
        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStatement = mock(PreparedStatement.class);

            driverManagerMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

            // 3. Suppose tableView selection model picks row 0
            // controller.removeRow(0)
            controller.removeRow(0);

            // 4. Verify user was removed from list
            assertFalse(users.contains(userToRemove), "User should be removed from the list");

            // 5. Verify DELETE query
            verify(mockConnection).prepareStatement(eq("DELETE FROM user WHERE userName = ?"));
            verify(mockStatement).setString(1, "bob123");
            verify(mockStatement).executeUpdate();
        }
    }

    @Test
    @DisplayName("removeRow(row) does nothing if row is invalid")
    void testRemoveRowInvalidIndex() {
        // No users in the list, so row 0 is invalid
        controller.removeRow(0);

        // The method checks `if (row >= 0 && row < tableView.getItems().size())`.
        // So no DB calls, no item removed.
        assertTrue(users.isEmpty(), "No user should be removed from an empty list");
    }
}
