package application.bookstore.controllers;

import application.bookstore.Exceptions.EmailAlreadyExistsException;
import application.bookstore.Exceptions.PasswordAlreadyExistsException;
import application.bookstore.Exceptions.UsernameAlreadyExistsException;
import application.bookstore.auxiliaries.DatabaseConnector;

import java.sql.*;

public class AddNewUserController implements DatabaseConnector {


    public AddNewUserController()
    {
    }

    public static void allValuesUnique(String username,String email) throws EmailAlreadyExistsException, UsernameAlreadyExistsException
    {
        if(doesValueExist("username",username))
            throw new UsernameAlreadyExistsException();
        else if(doesValueExist("email",email))
            throw new EmailAlreadyExistsException();

    }

    public static boolean doesValueExist(String columnName, String value) {
        String query = "SELECT COUNT(*) FROM user WHERE "+columnName+"= ?";
        boolean valueExists = false;

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, value);

            ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);//extracting the count value
                    valueExists = count > 0;
                }

        } catch (SQLException e) {
            System.out.println("Problem with DB");
            e.fillInStackTrace();
        }

        return valueExists;
    }


}
