package application.bookstore.charts;

import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.models.User;
import application.bookstore.views.AdminView;
import application.bookstore.views.BookView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Chart2 implements DatabaseConnector {
    private final User currentUser;

    public Chart2(User u)
    {
        this.currentUser=u;
    }

    public Scene showView(Stage primaryStage) {
        primaryStage.setTitle("Units Sold Statistics");
        BorderPane root = new BorderPane();
        ObservableList<PieChart.Data> unitsSoldChartData = getUnitsSoldData();

        PieChart unitsSoldChart = new PieChart(unitsSoldChartData);
        unitsSoldChart.setTitle("Units Sold Distribution");

        unitsSoldChart.getData().forEach(data -> {
            String bookName = data.getName();
            int unitsSold = (int) data.getPieValue(); // Assuming units sold is an integer
            data.setName(bookName + " - " + unitsSold + " units");
        });

        root.setCenter(unitsSoldChart);

        // Back, Next, Leave Buttons
        Button goBackButton = new Button("Back");
        goBackButton.setOnAction(e -> {
            Chart1 chart1=new Chart1(currentUser);
            primaryStage.setScene(chart1.showView(primaryStage));
        });

        goBackButton.setPrefWidth(100);
        goBackButton.setPadding(new Insets(20,20,20,20));

        goBackButton.setStyle(
                "-fx-background-color: orange; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 15px; "
        );

        Button nextChartButton = getNextChartButton(primaryStage);


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

        return new Scene(root, 1000, 700);
    }

    private Button getNextChartButton(Stage primaryStage) {
        Button nextChartButton = new Button("Next");
        nextChartButton.setOnAction(e -> {
            System.out.println("Next Chart clicked");
        });

        nextChartButton.setPrefWidth(100);
        nextChartButton.setPadding(new Insets(20,20,20,20));

        nextChartButton.setStyle(
                "-fx-background-color: orange; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 15px; "
        );

        nextChartButton.setOnAction(e->{
            Chart3 chart3=new Chart3(currentUser);
            primaryStage.setScene(chart3.showView(primaryStage));
        });
        return nextChartButton;
    }

    private ObservableList<PieChart.Data> getUnitsSoldData() {
        ObservableList<PieChart.Data> unitsSoldChartData = FXCollections.observableArrayList();

        try {
            // Establish database connection
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            Statement statement = connection.createStatement();

            // Retrieve data for units sold from the SoldBookType table
            String sql = "SELECT b.name AS bookName, sbt.soldQuantity AS unitsSold " +
                    "FROM SoldBookType sbt " +
                    "JOIN Book b ON sbt.ISBN = b.ISBN";

            ResultSet resultSet = statement.executeQuery(sql);

            // Add data to unitsSoldChartData
            while (resultSet.next()) {
                String bookName = resultSet.getString("bookName");
                int unitsSold = resultSet.getInt("unitsSold");

                unitsSoldChartData.add(new PieChart.Data(bookName, unitsSold));
            }

            // Close resources
            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.fillInStackTrace();
        }

        return unitsSoldChartData;
    }

}
