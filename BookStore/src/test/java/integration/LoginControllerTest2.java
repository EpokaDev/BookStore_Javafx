package integration;

import application.bookstore.controllers.LoginController;
import application.bookstore.models.User;
import application.bookstore.views.AdminView;
import application.bookstore.views.BookView;
import application.bookstore.views.LoginView;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class LoginControllerTest2 extends ApplicationTest {

    private LoginView loginView;
    private LoginController loginController;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        loginView = new LoginView(true);
        mockConnection = mock(Connection.class);

        Scene scene = loginView.showView(stage);
        stage.setScene(scene);

        // Initialize controller with mocked Connection
        loginController = new LoginController(loginView, stage, mockConnection);

        stage.show();
    }

    @BeforeEach
    public void setupMocks() throws Exception {
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Mock behavior for the connection
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Mock behavior for the ResultSet
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    public void testSuccessfulAdminLoginShowsAdminView() throws Exception {
        // Mocking admin user credentials
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("username")).thenReturn("admin");
        when(mockResultSet.getString("password")).thenReturn("admin123");
        when(mockResultSet.getString("Role")).thenReturn("admin");
        when(mockResultSet.getString("firstName")).thenReturn("Admin");
        when(mockResultSet.getString("lastName")).thenReturn("User");
        when(mockResultSet.getString("email")).thenReturn("admin@bookstore.com");
        when(mockResultSet.getString("gender")).thenReturn("Male");

        // Interact with the UI
        clickOn("#userTextField").write("admin");
        clickOn("#passwordField").write("admin123");
        clickOn("#loginButton");

        // Verify the logged-in user
        User loggedInUser = loginController.getLoggedInUser();
        assertNotNull(loggedInUser);
        assertEquals("Admin", loggedInUser.getFirstName());
        assertEquals("admin", loggedInUser.getUsername());
        assertEquals("admin", loggedInUser.getRoleString());

        // Verify SQL interactions
        verify(mockPreparedStatement, times(1)).setString(1, "admin");
        verify(mockPreparedStatement, times(1)).setString(2, "admin123");
        verify(mockPreparedStatement, times(1)).executeQuery();

        // Verify that AdminView is displayed by checking for a unique node
        verifySceneForAdminView();
    }

    @Test
    public void testSuccessfulLibrarianLoginShowsBookView() throws Exception {
        // Mocking librarian user credentials
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("username")).thenReturn("librarian");
        when(mockResultSet.getString("password")).thenReturn("lib123");
        when(mockResultSet.getString("Role")).thenReturn("librarian");
        when(mockResultSet.getString("firstName")).thenReturn("Lib");
        when(mockResultSet.getString("lastName")).thenReturn("Rarian");
        when(mockResultSet.getString("email")).thenReturn("lib@bookstore.com");
        when(mockResultSet.getString("gender")).thenReturn("Female");

        // Interact with the UI
        clickOn("#userTextField").write("librarian");
        clickOn("#passwordField").write("lib123");
        clickOn("#loginButton");

        // Verify the logged-in user
        User loggedInUser = loginController.getLoggedInUser();
        assertNotNull(loggedInUser);
        assertEquals("Lib", loggedInUser.getFirstName());
        assertEquals("librarian", loggedInUser.getUsername());
        assertEquals("librarian", loggedInUser.getRoleString());

        // Verify SQL interactions
        verify(mockPreparedStatement, times(1)).setString(1, "librarian");
        verify(mockPreparedStatement, times(1)).setString(2, "lib123");
        verify(mockPreparedStatement, times(1)).executeQuery();

        // Verify that BookView is displayed by checking for a unique node
        verifySceneForBookView();
    }

    @Test
    public void testSuccessfulManagerLoginShowsBookView() throws Exception {
        // Mocking manager user credentials
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("username")).thenReturn("manager");
        when(mockResultSet.getString("password")).thenReturn("mgr123");
        when(mockResultSet.getString("Role")).thenReturn("manager");
        when(mockResultSet.getString("firstName")).thenReturn("Manager");
        when(mockResultSet.getString("lastName")).thenReturn("User");
        when(mockResultSet.getString("email")).thenReturn("manager@bookstore.com");
        when(mockResultSet.getString("gender")).thenReturn("Male");

        // Interact with the UI
        clickOn("#userTextField").write("manager");
        clickOn("#passwordField").write("mgr123");
        clickOn("#loginButton");

        // Verify the logged-in user
        User loggedInUser = loginController.getLoggedInUser();
        assertNotNull(loggedInUser);
        assertEquals("Manager", loggedInUser.getFirstName());
        assertEquals("manager", loggedInUser.getUsername());
        assertEquals("manager", loggedInUser.getRoleString());

        // Verify SQL interactions
        verify(mockPreparedStatement, times(1)).setString(1, "manager");
        verify(mockPreparedStatement, times(1)).setString(2, "mgr123");
        verify(mockPreparedStatement, times(1)).executeQuery();

        // Verify that BookView is displayed by checking for a unique node
        verifySceneForBookView();
    }

    @Test
    public void testFailedLoginDoesNotChangeView() throws Exception {
        // Mock invalid credentials
        when(mockResultSet.next()).thenReturn(false);

        // Capture the initial scene
        Scene initialScene = primaryStage.getScene();

        // Interact with the UI
        clickOn("#userTextField").write("wrongUser");
        clickOn("#passwordField").write("wrongPass");
        clickOn("#loginButton");

        // Verify no user is logged in
        User loggedInUser = loginController.getLoggedInUser();
        assertNull(loggedInUser);

        // Verify SQL interactions
        verify(mockPreparedStatement, times(1)).setString(1, "wrongUser");
        verify(mockPreparedStatement, times(1)).setString(2, "wrongPass");
        verify(mockPreparedStatement, times(1)).executeQuery();

        // Verify that the scene has not changed
        assertEquals(initialScene, primaryStage.getScene());
    }

    @Test
    public void testEnterKeyTriggersLoginAndShowsCorrectView() throws Exception {
        // Mocking admin user credentials
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("username")).thenReturn("admin");
        when(mockResultSet.getString("password")).thenReturn("admin123");
        when(mockResultSet.getString("Role")).thenReturn("admin");
        when(mockResultSet.getString("firstName")).thenReturn("Admin");
        when(mockResultSet.getString("lastName")).thenReturn("User");
        when(mockResultSet.getString("email")).thenReturn("admin@bookstore.com");
        when(mockResultSet.getString("gender")).thenReturn("Male");

        // Interact with the UI using Enter key
        clickOn("#userTextField").write("admin");
        clickOn("#passwordField").write("admin123");
        type(KeyCode.ENTER);

        // Verify the logged-in user
        User loggedInUser = loginController.getLoggedInUser();
        assertNotNull(loggedInUser);
        assertEquals("admin", loggedInUser.getUsername());

        // Verify SQL interactions
        verify(mockPreparedStatement, times(1)).setString(1, "admin");
        verify(mockPreparedStatement, times(1)).setString(2, "admin123");
        verify(mockPreparedStatement, times(1)).executeQuery();

        // Verify that AdminView is displayed by checking for a unique node
        verifySceneForAdminView();
    }

    // Helper methods to verify the scene content

    private void verifySceneForAdminView() {
        // Example: Check if a node with fx:id "adminDashboard" is present
        // Adjust the node ID based on your AdminView implementation

        // Wait until the scene changes
        Scene currentScene = primaryStage.getScene();
        // Check for presence of a node unique to AdminView
        // For example, a Label with text "Admin Dashboard"

        // Using lookup with fx:id
        Node adminDashboard = lookup("#adminDashboard").query();

        assertNotNull(adminDashboard, "AdminView was not loaded.");
    }

    private void verifySceneForBookView() {
        // Example: Check if a node with fx:id "bookDashboard" is present
        // Adjust the node ID based on your BookView implementation

        // Wait until the scene changes
        Scene currentScene = primaryStage.getScene();
        // Check for presence of a node unique to BookView
        // For example, a Label with text "Book Dashboard"

        // Using lookup with fx:id
        Node bookDashboard = lookup("#bookDashboard").query();

        assertNotNull(bookDashboard, "BookView was not loaded.");
    }
}
