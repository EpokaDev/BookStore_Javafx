package unitTesting;

import application.bookstore.Exceptions.EmailAlreadyExistsException;
import application.bookstore.Exceptions.UsernameAlreadyExistsException;

import application.bookstore.controllers.AddNewUserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AddNewUserControllerTest {

    @BeforeEach
    void setUp() {
    }

    // -----------------------------------------
    // Nested test class for doesValueExist(...)
    // -----------------------------------------
    @Nested
    @DisplayName("Tests for doesValueExist()")
    class DoesValueExistTests {

        @Test
        @DisplayName("Should return false if value does NOT exist in DB")
        void testDoesValueExist_False() throws SQLException {
            // 1. Mock the static method call to simulate "value does not exist"
            try (MockedStatic<DriverManager> driverManagerMock = Mockito.mockStatic(DriverManager.class)) {
                // mock the Connection
                var mockConnection = Mockito.mock(java.sql.Connection.class);
                // mock the PreparedStatement
                var mockStatement = Mockito.mock(java.sql.PreparedStatement.class);
                // mock the ResultSet
                var mockResultSet = Mockito.mock(java.sql.ResultSet.class);

                // driverManagerMock: whenever DriverManager.getConnection(...) is called, return mockConnection
                driverManagerMock.when(() -> DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                        .thenReturn(mockConnection);

                // When we prepareStatement, return our mockStatement
                Mockito.when(mockConnection.prepareStatement(Mockito.anyString()))
                        .thenReturn(mockStatement);

                // When we executeQuery, return mockResultSet
                Mockito.when(mockStatement.executeQuery()).thenReturn(mockResultSet);

                // Suppose the DB returns 0 count
                Mockito.when(mockResultSet.next()).thenReturn(true);  // means we have one row
                Mockito.when(mockResultSet.getInt(1)).thenReturn(0); // count = 0

                boolean result = invokeDoesValueExist("username", "nonExistingUser");
                assertFalse(result, "Expected doesValueExist to return false for nonExistingUser");
            }
        }

        @Test
        @DisplayName("Should return true if value does exist in DB")
        void testDoesValueExist_True() {
            try (MockedStatic<DriverManager> driverManagerMock = Mockito.mockStatic(DriverManager.class)) {
                var mockConnection = Mockito.mock(java.sql.Connection.class);
                var mockStatement = Mockito.mock(java.sql.PreparedStatement.class);
                var mockResultSet = Mockito.mock(java.sql.ResultSet.class);

                driverManagerMock.when(() -> DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                        .thenReturn(mockConnection);

                Mockito.when(mockConnection.prepareStatement(Mockito.anyString()))
                        .thenReturn(mockStatement);

                Mockito.when(mockStatement.executeQuery()).thenReturn(mockResultSet);

                // This time, count = 1
                Mockito.when(mockResultSet.next()).thenReturn(true);
                Mockito.when(mockResultSet.getInt(1)).thenReturn(1);

                boolean result = invokeDoesValueExist("username", "existingUser");
                assertTrue(result, "Expected doesValueExist to return true for existingUser");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        @DisplayName("Should handle SQL exception and return false by default (or log error)")
        void testDoesValueExist_SQLException() throws Exception {
            try (MockedStatic<DriverManager> driverManagerMock = Mockito.mockStatic(DriverManager.class)) {
                var mockConnection = Mockito.mock(java.sql.Connection.class);

                driverManagerMock.when(() -> DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                        .thenThrow(new java.sql.SQLException("Simulated DB error"));

                // Because we simulate an exception, we expect the method to catch it and return false
                boolean result = invokeDoesValueExist("username", "whatever");
                assertFalse(result, "Expected doesValueExist to return false on SQLException");
            }
        }

        /**
         * Helper method to call the private method via reflection,
         * or you can make your method package-private for testing.
         */
        private boolean invokeDoesValueExist(String columnName, String value) {
            // Typically, you can do reflection to call a private static method
            // For brevity, you can test it directly if you make doesValueExist package-private
            //
            // E.g., in production code, set doesValueExist to protected or package-private
            // and put your test in the same package:
            //
            // return AddNewUserController.doesValueExist(columnName, value);

            try {
                var method = AddNewUserController.class.getDeclaredMethod("doesValueExist", String.class, String.class);
                method.setAccessible(true);
                return (boolean) method.invoke(null, columnName, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // -------------------------------------------------
    // Nested test class for allValuesUnique(...)
    // -------------------------------------------------
    @Nested
    @DisplayName("Tests for allValuesUnique()")
    class AllValuesUniqueTests {

        @Test
        @DisplayName("No exception when all values are unique")
        void testAllValuesUnique_AllUnique() {
            // Mock doesValueExist(...) to return false for all checks
            try (MockedStatic<AddNewUserController> controllerMock = Mockito.mockStatic(AddNewUserController.class, Mockito.CALLS_REAL_METHODS)) {
                // We want the real behavior for everything EXCEPT doesValueExist calls
                // So we just stub doesValueExist to always return false for the sake of this test.
                controllerMock.when(() -> AddNewUserController.doesValueExist("username", "uniqueU")).thenReturn(false);
                controllerMock.when(() -> AddNewUserController.doesValueExist("email", "uniqueE")).thenReturn(false);
                controllerMock.when(() -> AddNewUserController.doesValueExist("password", "uniqueP")).thenReturn(false);

                // The method should not throw any exception
                assertDoesNotThrow(() ->
                                AddNewUserController.allValuesUnique("uniqueU", "uniqueE"),
                        "Expected no exception if everything is unique"
                );
            }
        }

        @Test
        @DisplayName("Throw UsernameAlreadyExistsException if username already exists")
        void testAllValuesUnique_UsernameTaken() {
            try (MockedStatic<AddNewUserController> controllerMock = Mockito.mockStatic(AddNewUserController.class, Mockito.CALLS_REAL_METHODS)) {
                // The first check is username, so we say it returns true
                controllerMock.when(() -> AddNewUserController.doesValueExist("username", "existingUser"))
                        .thenReturn(true);

                // The rest are not checked if username already exists
                assertThrows(UsernameAlreadyExistsException.class, () ->
                        AddNewUserController.allValuesUnique("existingUser", "someEmail")
                );
            }
        }

        @Test
        @DisplayName("Throw EmailAlreadyExistsException if email already exists (and username is unique)")
        void testAllValuesUnique_EmailTaken() {
            try (MockedStatic<AddNewUserController> controllerMock = Mockito.mockStatic(AddNewUserController.class, Mockito.CALLS_REAL_METHODS)) {
                // username => false
                controllerMock.when(() -> AddNewUserController.doesValueExist("username", "uniqueU"))
                        .thenReturn(false);
                // email => true
                controllerMock.when(() -> AddNewUserController.doesValueExist("email", "existingEmail"))
                        .thenReturn(true);

                // The method should throw EmailAlreadyExistsException
                assertThrows(EmailAlreadyExistsException.class, () ->
                        AddNewUserController.allValuesUnique("uniqueU", "existingEmail")
                );
            }
        }
    }
}
