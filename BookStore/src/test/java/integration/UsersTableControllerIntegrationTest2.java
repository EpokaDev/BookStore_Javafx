package integration;

import application.bookstore.controllers.UsersTableController;
import application.bookstore.models.User;
import application.bookstore.views.UsersTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UsersTableControllerIntegrationTest2 extends ApplicationTest {

    private UsersTableView usersTableView;
    private UsersTableController controller;
    private ObservableList<User> users;

    @Mock
    private DataSource mockDataSource;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    private AutoCloseable mocks;

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize mocks
        mocks = MockitoAnnotations.openMocks(this);
        mockConnection = mock(Connection.class);


        // Set up the mock DataSource to return the mock Connection
        when(mockDataSource.getConnection()).thenReturn(mockConnection);

        // Initialize the ObservableList
        users = FXCollections.observableArrayList();

        // Initialize the view
        usersTableView = new UsersTableView(users, true);
        usersTableView.getAddButton().setId("addButton");
        usersTableView.getRemoveButton().setId("removeButton");

        // Initialize the controller with the mock DataSource
        controller = new UsersTableController(usersTableView, users, mockDataSource,mockConnection);
        usersTableView.setController(controller);

        // Set up the scene and stage
        Scene scene = new Scene(usersTableView, 800, 400);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Reset mocks before each test
        reset(mockDataSource, mockConnection, mockPreparedStatement);
    }

    @AfterAll
    static void afterAllTests() throws Exception {
        FxToolkit.hideStage();
    }

    @Test
    public void testAddUserFlow() throws Exception {
        // Mock the behavior of the PreparedStatement for INSERT
        when(mockConnection.prepareStatement(startsWith("INSERT INTO user")))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Click the Add button to open the dialog
        clickOn("#addButton");

        // Wait for the dialog to appear
        WaitForAsyncUtils.waitForFxEvents();

        // Interact with dialog fields
        clickOn("#firstNameField").write("Regi");
        clickOn("#lastNameField").write("Wonder");
        clickOn("#emailField").write("alice@wonder3.test");
        clickOn("#usernameField").write("regi");
        clickOn("#passwordField").write("regi1234");
        clickOn("#verifyPasswordField").write("regi1234");
        clickOn("#femaleRadio");  // Select "Female" gender
        clickOn("#adminRadio");   // Select "Admin" role

        // Click the OK button to submit
        clickOn("#okButton");

        // Wait for any background operations
        WaitForAsyncUtils.waitForFxEvents();
        WaitForAsyncUtils.sleep(3, TimeUnit.SECONDS);

        // Assertions to verify the user was added to the local list
        assertEquals(1, users.size(), "User list should contain one user after addition.");
        User added = users.get(0);
        assertEquals("Regi", added.getFirstName(), "First name should match the input.");
        assertEquals("Wonder", added.getLastName(), "Last name should match the input.");
        assertEquals("alice@wonder3.test", added.getEmail(), "Email should match the input.");
        assertEquals("regi", added.getUsername(), "Username should match the input.");
        assertEquals("regi1234", added.getPassword(), "Password should match the input.");
        assertEquals("female", added.getGender(), "Gender should match the input.");
        assertEquals("admin", added.getRoleString(), "Role should match the input.");

        // Verify DB interactions
        InOrder inOrder = inOrder(mockConnection, mockPreparedStatement);
        inOrder.verify(mockConnection).prepareStatement(
                "INSERT INTO user (firstName, lastName, email, userName, password, gender, Role) VALUES (?,?,?,?,?,?,?);"
        );
        inOrder.verify(mockPreparedStatement).setString(1, "Regi");
        inOrder.verify(mockPreparedStatement).setString(2, "Wonder");
        inOrder.verify(mockPreparedStatement).setString(3, "alice@wonder3.test");
        inOrder.verify(mockPreparedStatement).setString(4, "regi");
        inOrder.verify(mockPreparedStatement).setString(5, "regi1234");
        inOrder.verify(mockPreparedStatement).setString(6, "female");
        inOrder.verify(mockPreparedStatement).setString(7, "admin");
        inOrder.verify(mockPreparedStatement).executeUpdate();
        inOrder.verify(mockPreparedStatement).close();

        // Ensure no other interactions occurred
        verifyNoMoreInteractions(mockPreparedStatement, mockConnection);
    }

    /**
     * Test removing a user. We'll pre-populate the table (and thus users list)
     * and then verify that clicking "Remove" calls the DELETE statement.
     */
    @Test
    public void testRemoveUserFlow() throws Exception {
        // 1) Add a user to the local list, so it's displayed in the table
        User bob = new User(
                new SimpleStringProperty("Bob"),
                new SimpleStringProperty("Builder"),
                new SimpleStringProperty("Bob@gmail.com"),
                new SimpleStringProperty("bobby"),
                new SimpleStringProperty("123"),
                new SimpleStringProperty("male"),
                new SimpleStringProperty("admin")
        );
        users.add(bob);
        // Typically, the table will show 1 row now

        // 2) Mock the DB calls for DELETE
        when(mockConnection.prepareStatement(startsWith("DELETE FROM user")))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // 3) Select the row in the table. In a real UI scenario, we might do:
        // Selecting the first user in the table
        interact(() -> usersTableView.getTableView().getSelectionModel().select(0));

        // 4) Click remove button
        clickOn("#removeButton");

        // Wait for any background operations
        WaitForAsyncUtils.waitForFxEvents();
        WaitForAsyncUtils.sleep(1, TimeUnit.SECONDS);

        // 5) The user should be removed from the local list
        assertTrue(users.isEmpty(), "User list should be empty after removal.");

        // 6) Verify DB call
        InOrder inOrder = inOrder(mockConnection, mockPreparedStatement);
        inOrder.verify(mockConnection).prepareStatement("DELETE FROM user WHERE userName = ?");
        inOrder.verify(mockPreparedStatement).setString(1, "bobby");
        inOrder.verify(mockPreparedStatement).executeUpdate();
        inOrder.verify(mockPreparedStatement).close();

        // Ensure no other interactions occurred
        verifyNoMoreInteractions(mockPreparedStatement, mockConnection);
    }

    @Test
    public void testUpdateUserFlow() throws Exception {
        // 1) Add a user to the local list, so it's displayed in the table
        User alice = new User(
                new SimpleStringProperty("Alice"),
                new SimpleStringProperty("Wonderland"),
                new SimpleStringProperty("alice@example.com"),
                new SimpleStringProperty("aliceW"),
                new SimpleStringProperty("password123"),
                new SimpleStringProperty("female"),
                new SimpleStringProperty("user")
        );
        users.add(alice);
        // Typically, the table will show 1 row now

        // 2) Mock the DB calls for UPDATE
        when(mockConnection.prepareStatement(startsWith("UPDATE user SET")))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // 3) Simulate editing the 'firstName' cell
        String appendToFirstName = "na";
        String newFirstName = alice.getFirstName()+appendToFirstName;

        // Assuming you are using TestFX for UI interactions
        // Focus on the first cell of the first row in 'firstNameColumn'
        TableColumn<User, String> firstNameColumn = usersTableView.getFirstNameColumn();
        // The exact method to edit a cell may vary based on your setup
        // Here's a general approach:

        // Select the cell
        interact(() -> {
            usersTableView.getTableView().edit(0, firstNameColumn);
            usersTableView.getTableView().getSelectionModel().select(0);
        });

        // Simulate typing the new first name
        // This part depends on the testing framework you're using
        // Here's a pseudo-code example:
        // clickOn the cell, erase existing text, type appendToFirstName, press Enter

        // Example with TestFX:
        clickOn(firstNameColumn.getCellData(0));
        write(appendToFirstName);
        type(javafx.scene.input.KeyCode.ENTER);

        // 4) Wait for any background operations
        WaitForAsyncUtils.waitForFxEvents();
        WaitForAsyncUtils.sleep(1, TimeUnit.SECONDS);

        // 5) The user should have the updated first name in the local list
        assertEquals(newFirstName, users.get(0).getFirstName());

        // 6) Verify DB call
        String expectedQuery = "UPDATE user SET firstName = ? WHERE userName = ?";
        InOrder inOrder = inOrder(mockConnection, mockPreparedStatement);
        inOrder.verify(mockConnection).prepareStatement(expectedQuery);
        inOrder.verify(mockPreparedStatement).setString(1, newFirstName);
        inOrder.verify(mockPreparedStatement).setString(2, "aliceW");
        inOrder.verify(mockPreparedStatement).executeUpdate();
        inOrder.verify(mockPreparedStatement).close();

        // Ensure no other interactions occurred
        verifyNoMoreInteractions(mockPreparedStatement, mockConnection);
    }

    @AfterAll
    static void tearDown() throws Exception {
        FxToolkit.hideStage();
    }
}
