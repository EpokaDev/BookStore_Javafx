package system;

import application.bookstore.models.User;
import application.bookstore.views.AdminView;
import application.bookstore.views.LoginView;
import application.bookstore.views.UsersTableView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testfx.api.FxAssert;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BookstoreSystemTest extends ApplicationTest {

    private DataSource mockDataSource;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize mocks
        mockDataSource = mock(DataSource.class);
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Mock DataSource behavior
        when(mockDataSource.getConnection()).thenReturn(mockConnection);

        // Initialize the main application with mocked DataSource
        //BookstoreApplication mainApp = new BookstoreApplication(mockDataSource);
       // Scene scene = mainApp.startApplication(stage); // Assumes startApplication initializes the main view
        //stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void setUpMocks() throws Exception {
        // Reset mocks before each test
        reset(mockDataSource, mockConnection, mockPreparedStatement, mockResultSet);
    }

    @AfterAll
    public static void tearDown() throws Exception {
        FxToolkit.hideStage();
    }

    /**
     * Helper method to mock successful login
     */
    private void mockSuccessfulLogin() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("username")).thenReturn("admin");
        when(mockResultSet.getString("password")).thenReturn("admin123");
        when(mockResultSet.getString("Role")).thenReturn("admin");
        when(mockResultSet.getString("firstName")).thenReturn("Admin");
        when(mockResultSet.getString("lastName")).thenReturn("User");
        when(mockResultSet.getString("email")).thenReturn("admin@bookstore.com");
        when(mockResultSet.getString("gender")).thenReturn("Male");
    }

    /**
     * Helper method to mock failed login
     */
    private void mockFailedLogin() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
    }

    @Test
    @DisplayName("Test Successful Login and Navigation to AdminView")
    public void testSuccessfulLoginAndNavigation() throws Exception {
        // Arrange: Mock successful login
        mockSuccessfulLogin();

        // Act: Perform login
        clickOn("#userTextField").write("admin");
        clickOn("#passwordField").write("admin123");
        clickOn("#loginButton");

        // Assert: Verify AdminView is displayed
        FxAssert.verifyThat("#adminView", NodeMatchers.isVisible());

        // Verify SQL interactions
        verify(mockConnection, times(1)).prepareStatement(anyString());
        verify(mockPreparedStatement, times(1)).setString(1, "admin");
        verify(mockPreparedStatement, times(1)).setString(2, "admin123");
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    @DisplayName("Test Failed Login")
    public void testFailedLogin() throws Exception {
        // Arrange: Mock failed login
        mockFailedLogin();

        // Act: Attempt login with invalid credentials
        clickOn("#userTextField").write("wrongUser");
        clickOn("#passwordField").write("wrongPass");
        clickOn("#loginButton");

        // Assert: Verify error message is shown and still on LoginView
        FxAssert.verifyThat("#loginView", NodeMatchers.isVisible());
        FxAssert.verifyThat("#errorLabel", NodeMatchers.isVisible());

        // Verify SQL interactions
        verify(mockConnection, times(1)).prepareStatement(anyString());
        verify(mockPreparedStatement, times(1)).setString(1, "wrongUser");
        verify(mockPreparedStatement, times(1)).setString(2, "wrongPass");
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    @DisplayName("Test Navigation to UsersTableView and Back")
    public void testNavigateToUsersTableViewAndBack() throws Exception {
        // Arrange: Mock successful login
        mockSuccessfulLogin();

        // Act: Perform login
        clickOn("#userTextField").write("admin");
        clickOn("#passwordField").write("admin123");
        clickOn("#loginButton");

        // Assert: Verify AdminView is displayed
        FxAssert.verifyThat("#adminView", NodeMatchers.isVisible());

        // Act: Click on "Other Users" button
        clickOn("#otherUsersButton");

        // Assert: Verify UsersTableView is displayed
        FxAssert.verifyThat("#usersTableView", NodeMatchers.isVisible());

        // Act: Click on "Back" button
        clickOn("#backButton");

        // Assert: Verify AdminView is displayed again
        FxAssert.verifyThat("#adminView", NodeMatchers.isVisible());

        // Verify navigation
        verify(mockConnection, atLeastOnce()).prepareStatement(anyString());
    }

    @Test
    @DisplayName("Test Adding a New User")
    public void testAddNewUser() throws Exception {
        // Arrange: Mock successful login and add user operations
        mockSuccessfulLogin();

        // Mock add user SQL
        PreparedStatement mockInsertStmt = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(startsWith("INSERT INTO user")))
                .thenReturn(mockInsertStmt);
        when(mockInsertStmt.executeUpdate()).thenReturn(1);

        // Act: Perform login
        clickOn("#userTextField").write("admin");
        clickOn("#passwordField").write("admin123");
        clickOn("#loginButton");

        // Navigate to UsersTableView
        clickOn("#otherUsersButton");

        // Click "Add User" button
        clickOn("#addUserButton");

        // Fill in Add New User Dialog
        clickOn("#firstNameField").write("John");
        clickOn("#lastNameField").write("Doe");
        clickOn("#emailField").write("john.doe@example.com");
        clickOn("#usernameField").write("johndoe");
        clickOn("#passwordField").write("password123");
        clickOn("#verifyPasswordField").write("password123");
        clickOn("#maleRadio"); // Select gender
        clickOn("#userRoleRadio"); // Select role

        // Submit the dialog
        clickOn("#okButton");

        // Assert: Verify the new user is added to the table
        FxAssert.verifyThat("#usersTableView", NodeMatchers.isVisible());
        // You might need to add a delay or wait for async operations
        // Alternatively, verify through the mock or observable list

        // Verify SQL interactions
        verify(mockConnection, times(1)).prepareStatement(startsWith("INSERT INTO user"));
        verify(mockInsertStmt, times(1)).setString(1, "John");
        verify(mockInsertStmt, times(1)).setString(2, "Doe");
        verify(mockInsertStmt, times(1)).setString(3, "john.doe@example.com");
        verify(mockInsertStmt, times(1)).setString(4, "johndoe");
        verify(mockInsertStmt, times(1)).setString(5, "password123");
        verify(mockInsertStmt, times(1)).setString(6, "male");
        verify(mockInsertStmt, times(1)).setString(7, "user");
        verify(mockInsertStmt, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("Test Removing a User")
    public void testRemoveUser() throws Exception {
        // Arrange: Mock successful login and remove user operations
        mockSuccessfulLogin();

        // Mock delete user SQL
        PreparedStatement mockDeleteStmt = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(startsWith("DELETE FROM user")))
                .thenReturn(mockDeleteStmt);
        when(mockDeleteStmt.executeUpdate()).thenReturn(1);

        // Act: Perform login
        clickOn("#userTextField").write("admin");
        clickOn("#passwordField").write("admin123");
        clickOn("#loginButton");

        // Navigate to UsersTableView
        clickOn("#otherUsersButton");

        // Select a user in the table
        // Assuming there's at least one user; otherwise, you'd need to add one first
        // For testing, you can pre-populate the users list or mock the table items
        // Here, we assume the table has a user with username "johndoe"

        // Select the user (modify based on your table setup)
        clickOn("johndoe"); // Click on the row containing "johndoe"

        // Click "Remove User" button
        clickOn("#removeUserButton");

        // Assert: Verify the user is removed from the table
        //---FxAssert.verifyThat("johndoe", NodeMatchers.isNotVisible());

        // Verify SQL interactions
        verify(mockConnection, times(1)).prepareStatement(startsWith("DELETE FROM user"));
        verify(mockDeleteStmt, times(1)).setString(1, "johndoe");
        verify(mockDeleteStmt, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("Test Logout Functionality")
    public void testLogout() throws Exception {
        // Arrange: Mock successful login
        mockSuccessfulLogin();

        // Act: Perform login
        clickOn("#userTextField").write("admin");
        clickOn("#passwordField").write("admin123");
        clickOn("#loginButton");

        // Assert: Verify AdminView is displayed
        FxAssert.verifyThat("#adminView", NodeMatchers.isVisible());

        // Act: Click on "Logout" button
        clickOn("#logoutButton");

        // Assert: Verify returned to LoginView
        FxAssert.verifyThat("#loginView", NodeMatchers.isVisible());

        // Optionally, verify that user session is cleared
        // This depends on your application's implementation
    }

    @Test
    @DisplayName("End-to-End Test: Login, Add User, Remove User, Logout")
    public void testEndToEndWorkflow() throws Exception {
        // Arrange: Mock successful login, add, and remove user operations
        mockSuccessfulLogin();

        // Mock add user SQL
        PreparedStatement mockInsertStmt = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(startsWith("INSERT INTO user")))
                .thenReturn(mockInsertStmt);
        when(mockInsertStmt.executeUpdate()).thenReturn(1);

        // Mock delete user SQL
        PreparedStatement mockDeleteStmt = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(startsWith("DELETE FROM user")))
                .thenReturn(mockDeleteStmt);
        when(mockDeleteStmt.executeUpdate()).thenReturn(1);

        // Act: Perform login
        clickOn("#userTextField").write("admin");
        clickOn("#passwordField").write("admin123");
        clickOn("#loginButton");

        // Navigate to UsersTableView
        clickOn("#otherUsersButton");

        // Add a new user
        clickOn("#addUserButton");
        clickOn("#firstNameField").write("Jane");
        clickOn("#lastNameField").write("Doe");
        clickOn("#emailField").write("jane.doe@example.com");
        clickOn("#usernameField").write("janedoe");
        clickOn("#passwordField").write("password456");
        clickOn("#verifyPasswordField").write("password456");
        clickOn("#femaleRadio"); // Select gender
        clickOn("#adminRadio"); // Select role
        clickOn("#okButton");

        // Assert: Verify the new user is added
        FxAssert.verifyThat("#usersTableView", NodeMatchers.isVisible());
        // Depending on your implementation, verify the user appears in the table

        // Remove the newly added user
        clickOn("janedoe"); // Select the newly added user
        clickOn("#removeUserButton");

        // Assert: Verify the user is removed
        //---FxAssert.verifyThat("janedoe", NodeMatchers.isNotVisible());

        // Logout
        clickOn("#logoutButton");

        // Assert: Returned to LoginView
        FxAssert.verifyThat("#loginView", NodeMatchers.isVisible());

        // Verify SQL interactions
        // Add User
        verify(mockConnection, times(1)).prepareStatement(startsWith("INSERT INTO user"));
        verify(mockInsertStmt, times(1)).setString(1, "Jane");
        verify(mockInsertStmt, times(1)).setString(2, "Doe");
        verify(mockInsertStmt, times(1)).setString(3, "jane.doe@example.com");
        verify(mockInsertStmt, times(1)).setString(4, "janedoe");
        verify(mockInsertStmt, times(1)).setString(5, "password456");
        verify(mockInsertStmt, times(1)).setString(6, "female");
        verify(mockInsertStmt, times(1)).setString(7, "admin");
        verify(mockInsertStmt, times(1)).executeUpdate();

        // Remove User
        verify(mockConnection, times(1)).prepareStatement(startsWith("DELETE FROM user"));
        verify(mockDeleteStmt, times(1)).setString(1, "janedoe");
        verify(mockDeleteStmt, times(1)).executeUpdate();
    }
}
