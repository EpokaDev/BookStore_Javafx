package application.bookstore.controllers;

import application.bookstore.auxiliaries.Alerts;
import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.models.Book;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import java.sql.*;
import java.util.ArrayList;

public class BookList implements DatabaseConnector {
    ArrayList<Book> books = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>();
    static ArrayList<Book> booksWithLowQuantity = new ArrayList<>();
    public ArrayList<Book> getBooks() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Book");
            categories.add("All");
            while (resultSet.next()) {
                Book book = new Book(resultSet.getString("ISBN"), resultSet.getString("name"),
                        resultSet.getString("author"), resultSet.getString("category"),
                        resultSet.getInt("supplierId") ,resultSet.getString("description"),
                        new Image(resultSet.getString("bookURL")), resultSet.getDouble("original_price"),
                        resultSet.getDouble("selling_price"), resultSet.getInt("quantity"));
                books.add(book);
                categories.add(book.getCategory());
                if(book.getQuantity() < 5){
                    booksWithLowQuantity.add(book);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return books;
    }
    public static  void notifyLowQuantity() {
        StringBuilder notify = new StringBuilder();
        notify.append("Low Quantity for the following books:\n");
        int i = 1;
        for (Book book : booksWithLowQuantity) {
            notify.append(i++).append(") ")
                    .append(book.getTitle())
                    .append(" Quantity: ").append(book.getQuantity())
                    .append("\n");
        }
        if (!(booksWithLowQuantity.isEmpty())) {
            Alerts.showAlert(Alert.AlertType.INFORMATION, "Low Quantity", notify.toString());
        }
    }
    public ArrayList<String> getCategories(){
        return  categories;
    }
}

