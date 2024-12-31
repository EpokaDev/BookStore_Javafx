package application.bookstore.controllers;

import application.bookstore.Exceptions.*;
import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.models.User;

import java.sql.*;

public class ProfileViewEditController implements DatabaseConnector {
    public static final int accessCode = 1234;

    public static void changeName(String new_value , User user){
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String updateQuery = "UPDATE User SET firstName = ? WHERE email = ? and password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, new_value);
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setString(3, user.getPassword());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("First name updated successfully");
                    user.setFirstName(new_value);
                } else {
                    System.out.println("Name: User not found or no updates performed");
                }
            }
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
    }

    public static void changeSurname(String new_value , User user){
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String updateQuery = "UPDATE User SET lastName = ? WHERE email = ? and password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, new_value);
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setString(3, user.getPassword());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Last name updated successfully");
                    user.setLastName(new_value);
                } else {
                    System.out.println("Last Name: User not found or no updates performed");
                }
            }
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
    }


    public static void changeUsername(String new_value, User user) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            // Check if the new username already exists for any user except the current user
            String checkQuery = "SELECT COUNT(*) FROM User WHERE userName = ? AND (email != ? OR password != ?)";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, new_value);
                checkStatement.setString(2, user.getEmail());
                checkStatement.setString(3, user.getPassword());

                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);

                if (count > 0) {
                    throw new UsernameAlreadyExistsException();
                } else {
                    // Update username if it doesn't exist for any other user
                    String updateQuery = "UPDATE User SET userName = ? WHERE email = ? AND password = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                        preparedStatement.setString(1, new_value);
                        preparedStatement.setString(2, user.getEmail());
                        preparedStatement.setString(3, user.getPassword());

                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Username updated successfully");
                            user.setUsername(new_value);
                        } else {
                            System.out.println("Username: User not found or no updates performed");
                        }
                    }
                }
            }
        } catch (SQLException | UsernameAlreadyExistsException e) {
            e.fillInStackTrace();
        }
    }


    public static void changeEmail(String new_value, User user) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            // Check if the new email already exists for any user except the current user
            String checkQuery = "SELECT COUNT(*) FROM User WHERE email = ? AND (firstName != ? OR lastName != ? OR password != ?)";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, new_value);
                checkStatement.setString(2, user.getFirstName());
                checkStatement.setString(3, user.getLastName());
                checkStatement.setString(4, user.getPassword());

                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);

                if (count > 0) {
                    throw new EmailAlreadyExistsException();
                } else {
                    // Update email if it doesn't exist for any other user
                    String updateQuery = "UPDATE User SET email = ? WHERE firstName = ? AND lastName = ? AND password = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                        preparedStatement.setString(1, new_value);
                        preparedStatement.setString(2, user.getFirstName());
                        preparedStatement.setString(3, user.getLastName());
                        preparedStatement.setString(4, user.getPassword());

                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Email updated successfully");
                            user.setEmail(new_value);
                        } else {
                            System.out.println("Email: User not found or no updates performed");
                        }
                    }
                }
            }
        } catch (SQLException | EmailAlreadyExistsException e) {
            e.fillInStackTrace();
        }
    }


    public static void changeGender(String new_value , User user){
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
                String updateQuery = "UPDATE User SET gender = ? WHERE email = ? and password = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    preparedStatement.setString(1, new_value);
                    preparedStatement.setString(2, user.getEmail());
                    preparedStatement.setString(3, user.getPassword());

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Gender updated successfully");
                        user.setGender(new_value);
                    } else {
                        System.out.println("Gender: User not found or no updates performed");
                    }
                }
            } catch (SQLException e) {
                e.fillInStackTrace();
            }
    }

    public static void changePassword(String new_value, User user){
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            // Check if the new password already exists for any user except the current user
            String checkQuery = "SELECT COUNT(*) FROM User WHERE password = ? AND (email != ? OR (firstName != ? AND lastName != ?))";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, new_value);
                checkStatement.setString(2, user.getEmail());
                checkStatement.setString(3, user.getFirstName());
                checkStatement.setString(4, user.getLastName());

                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);

                if (count > 0) {
                    throw new PasswordAlreadyExistsException();
                } else {
                    // Update password if it doesn't exist for any other user
                    String updateQuery = "UPDATE User SET password = ? WHERE firstName = ? AND lastName = ? AND email = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                        preparedStatement.setString(1, new_value);
                        preparedStatement.setString(2, user.getFirstName());
                        preparedStatement.setString(3, user.getLastName());
                        preparedStatement.setString(4, user.getEmail());
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Password updated successfully");
                            user.setPassword(new_value);
                        } else {
                            System.out.println("Password: User not found or no updates performed");
                        }
                    }
                }
            }
        } catch (SQLException | PasswordAlreadyExistsException e) {
            e.fillInStackTrace();
        }
    }
}
