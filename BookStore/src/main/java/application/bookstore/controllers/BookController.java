package application.bookstore.controllers;

import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.models.Book;
import application.bookstore.models.User;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class BookController implements DatabaseConnector {

    public static void generateBill(User user, ObservableList<Book> selectedBooks, double amount) {
        File billsFolder = new File("bills");
        if (!billsFolder.exists()) {
            billsFolder.mkdir();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String fileName = "bills/bill_" + timestamp + ".txt";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Sold by: " + user.getFirstName() + " " + user.getLastName() + " Role: " + user.getRoleString() + "\n\n");
            writer.write("Books:\n");
            for (Book book : selectedBooks) {
                String bookInfo = book.getTitle() + " - $" + book.getSellingPrice() + "\n";
                writer.write("- " + book.getTitle() + " (Chosen Quantity: " + book.getChosenQuantity() + "): $" + book.getSellingPrice());
                writer.write(bookInfo);
            }
            writer.write("\nTotal Amount: $" + amount);
            updateQuantity(selectedBooks);

            System.out.println("Bill generated successfully. Filename: " + fileName);

        } catch (IOException e) {
            System.err.println("Error generating bill: " + e.getMessage());
        }
    }

    public static void updateQuantity(ObservableList<Book> selectedBooks) {
        for (Book book : selectedBooks) {
            String updateSQL = "UPDATE book SET quantity = ? WHERE ISBN = ?";
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
                int newQuantity = book.getQuantity() - book.getChosenQuantity();
                preparedStatement.setInt(1, newQuantity);
                preparedStatement.setString(2, book.getISBN());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void deleteBook(String isbn) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String deleteSql = "DELETE FROM Book WHERE ISBN = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
                preparedStatement.setString(1, isbn);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void generateBillToDatabase(ObservableList<Book> selectedBooks, double amount, User user) {
        if(amount < 0){
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime timestamp = LocalDateTime.now();

            String createSQL = "INSERT INTO Bill (date, username, total_amount) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(createSQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setObject(1, timestamp);
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setDouble(3, amount);
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            int orderId;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve auto-generated keys.");
            }

            for (Book book : selectedBooks) {
                String createNewSQL = "INSERT INTO SoldBookType (ISBN, amount, soldQuantity, orderId) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedNewStatement = connection.prepareStatement(createNewSQL);
                preparedNewStatement.setString(1, book.getISBN());
                preparedNewStatement.setDouble(2, (book.getSellingPrice() - book.getOriginalPrice()));
                preparedNewStatement.setInt(3, book.getChosenQuantity());
                preparedNewStatement.setInt(4, orderId);
                preparedNewStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

