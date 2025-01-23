package integration;

import application.bookstore.controllers.LoginController;
import application.bookstore.models.User;
import application.bookstore.views.LoginView;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
/*
This test tests the interaction between LoginController, LoginView and User model
3 cases are tested:
1) Successful login
2) Failed login
3) Successful login triggered by pressing enter key
4) Failed login triggered by pressing the enter key
 */

public class LoginControllerTest extends ApplicationTest {

    private LoginView loginView;
    private LoginController loginController;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @Override
    public void start(Stage stage) {
        loginView = new LoginView(true);
        mockConnection = mock(Connection.class);
        Scene scene = loginView.showView(stage); // setting up the UI nodes including btn
        stage.setScene(scene);

        // mock-based controller
        loginController = new LoginController(loginView, stage, mockConnection);

        stage.show();
    }

    @BeforeEach
    public void setupMocks() throws Exception {
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // mock behavior for the connection
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // mock behavior for the ResultSet
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        // Mocking valid username and password
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("username")).thenReturn("admin");
        when(mockResultSet.getString("password")).thenReturn("admin123");
        when(mockResultSet.getString("Role")).thenReturn("admin");
        when(mockResultSet.getString("firstName")).thenReturn("Admin");
        when(mockResultSet.getString("lastName")).thenReturn("User");
        when(mockResultSet.getString("email")).thenReturn("admin@bookstore.com");
        when(mockResultSet.getString("gender")).thenReturn("Male");

        // Interaction with the UI
        clickOn("#userTextField").write("admin");
        clickOn("#passwordField").write("admin123");
        clickOn("#loginButton");

        // Verifying the logged-in user
        User loggedInUser = loginController.getLoggedInUser();
        assertEquals("Admin", loggedInUser.getFirstName());
        assertEquals("admin", loggedInUser.getUsername());
        assertEquals("admin", loggedInUser.getRoleString());

        // Verify SQL interactions
        verify(mockPreparedStatement, times(1)).setString(1, "admin");
        verify(mockPreparedStatement, times(1)).setString(2, "admin123");
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testFailedLogin() throws Exception {
        // Mock invalid credentials
        when(mockResultSet.next()).thenReturn(false);

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
    }

    @Test
    public void testEnterKeyTriggersLogin() throws Exception {
        // Mock valid credentials
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("username")).thenReturn("admin");
        when(mockResultSet.getString("password")).thenReturn("admin123");
        when(mockResultSet.getString("Role")).thenReturn("admin");

        // Interact with the UI using Enter key
        clickOn("#userTextField").write("admin");
        clickOn("#passwordField").write("admin123");
        type(KeyCode.ENTER);

        // Verify the logged-in user
        User loggedInUser = loginController.getLoggedInUser();
        assertEquals("admin", loggedInUser.getUsername());

        // Verify SQL interactions
        verify(mockPreparedStatement, times(1)).setString(1, "admin");
        verify(mockPreparedStatement, times(1)).setString(2, "admin123");
        verify(mockPreparedStatement, times(1)).executeQuery();
    }
}
