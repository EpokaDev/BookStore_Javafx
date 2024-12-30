package application.bookstore.charts;

import application.bookstore.views.BookView;
import javafx.scene.Scene;
import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.models.User;
import application.bookstore.views.AdminView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Chart3 implements DatabaseConnector{
    private final User currentUser;

    public Chart3(User u)
    {
        this.currentUser=u;
    }

    public Scene showView(Stage primaryStage)
    {
        BorderPane root = new BorderPane();
        ObservableList<PieChart.Data> supplierBooksData = getSupplierBooksData();

        PieChart supplierBooksChart = new PieChart(supplierBooksData);
        supplierBooksChart.setTitle("Number of Books Supplied by Each Supplier");

        supplierBooksChart.getData().forEach(data -> {
            String supplierName = data.getName();
            int booksSupplied = (int) data.getPieValue();
            data.setName(supplierName + " - " + booksSupplied + " books");
        });

        root.setCenter(supplierBooksChart);

        // Back, Next, Leave Buttons
        // (Same button styling and layout as in the previous methods)

        // Back, Next, Leave Buttons
        Button goBackButton = new Button("Back");
        goBackButton(primaryStage, goBackButton, currentUser);

        Button nextChartButton = new Button();
        nextChartButton.setPrefWidth(100);
        nextChartButton.setOpacity(0);

        HBox buttonsBox = new HBox(10, goBackButton, nextChartButton);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setSpacing(700);
        root.setBottom(buttonsBox);

        Button leaveButton = new Button("Leave");
        leaveButton.setPrefWidth(100);
        leaveButton.setPadding(new Insets(20, 20, 20, 20));
        leaveButton.setStyle(
                "-fx-background-color: red; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 15px; "
        );
        root.setTop(leaveButton);
        BorderPane.setAlignment(leaveButton, Pos.TOP_LEFT);

        leaveButton.setOnAction(e->
        {
            if(currentUser.getRoleString().equalsIgnoreCase("admin"))
            {
                AdminView adminView=new AdminView(currentUser);
                try {
                    primaryStage.setScene(adminView.showView(primaryStage));
                } catch (Exception ex) {
                    System.out.println("Problem when accessing adminView");
                }
            }else {
                BookView bookView = new BookView(currentUser.getRole(), currentUser);
                try {
                    primaryStage.setScene(bookView.showView(primaryStage));
                } catch (Exception ex) {
                    System.out.println("Problem when accessing BookStore");
                }
            }});

        root.setTop(leaveButton);
        BorderPane.setAlignment(leaveButton, Pos.TOP_LEFT);

        return new Scene(root, 1000, 700);
    }

    static void goBackButton(Stage primaryStage, Button goBackButton, User currentUser) {
        goBackButton.setOnAction(e -> {
            Chart2 chart2=new Chart2(currentUser);
            primaryStage.setScene(chart2.showView(primaryStage));
        });

        goBackButton.setPrefWidth(100);
        goBackButton.setPadding(new Insets(20,20,20,20));

        goBackButton.setStyle(
                "-fx-background-color: orange; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 15px; "
        );
    }

    private ObservableList<PieChart.Data> getSupplierBooksData() {
        ObservableList<PieChart.Data> supplierBooksData = FXCollections.observableArrayList();

        try {
            // Establish database connection
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            Statement statement = connection.createStatement();

            // Retrieve data for the number of books supplied by each supplier from the Supplies table
            String sql = "SELECT s.name AS supplierName, COUNT(su.ISBN) AS booksSupplied " +
                    "FROM Supplier s " +
                    "JOIN supplies su ON s.SupplierId = su.SupplierId " +
                    "GROUP BY s.SupplierId";

            ResultSet resultSet = statement.executeQuery(sql);

            // Add data to supplierBooksData
            while (resultSet.next()) {
                String supplierName = resultSet.getString("supplierName");
                int booksSupplied = resultSet.getInt("booksSupplied");

                supplierBooksData.add(new PieChart.Data(supplierName, booksSupplied));
            }

            // Close resources
            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.fillInStackTrace();
        }

        return supplierBooksData;
    }

}
