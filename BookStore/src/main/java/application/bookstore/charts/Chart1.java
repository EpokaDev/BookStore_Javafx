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
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Chart1 implements DatabaseConnector {


    private final User currentUser;
    public Chart1(User u)
    {
        this.currentUser=u;
    }

    public Scene showView(Stage primaryStage) {
        primaryStage.setTitle("Statistics");
        BorderPane root=new BorderPane();
        ObservableList<PieChart.Data> pieChartData = getBookProfitData();

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Book Profit Distribution");

        pieChart.getData().forEach(data -> {
            String bookName = data.getName();
            double profit = data.getPieValue();
            data.setName(bookName + " - $" + String.format("%.2f", profit));
        });

        root.setCenter(pieChart);

        //Back,Next Leave Buttons
        Button emptyBtn=new Button();
        emptyBtn.setPrefWidth(100);
        emptyBtn.setOpacity(0);

        Button nextChartButton = getNextChartButton(primaryStage);

        HBox buttonsBox = new HBox(10, emptyBtn, nextChartButton);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setSpacing(700);
        root.setBottom(buttonsBox);

        Button leaveButton=new Button("Leave");
        leaveButton.setPrefWidth(100);
        leaveButton.setPadding(new Insets(20,20,20,20));

        leaveButton.setStyle(
                "-fx-background-color: red; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 15px; "
        );
        root.setTop(leaveButton);
        BorderPane.setAlignment(leaveButton,Pos.TOP_LEFT);

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
        Chart3.goBackButton(primaryStage, nextChartButton, currentUser);
        return nextChartButton;
    }

    private ObservableList<PieChart.Data> getBookProfitData() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try {
            // Establish database connection
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            Statement statement = connection.createStatement();

            // Retrieve data from the SoldBookType and Book tables using a JOIN
            String sql = "SELECT b.name AS bookName, sbt.amount * sbt.soldQuantity AS totalProfit " +
                    "FROM SoldBookType sbt " +
                    "JOIN Book b ON sbt.ISBN = b.ISBN";

            ResultSet resultSet = statement.executeQuery(sql);

            // Calculate profit for each book and add to pieChartData
            while (resultSet.next()) {
                String bookName = resultSet.getString("bookName");
                double totalProfit = resultSet.getDouble("totalProfit");

                pieChartData.add(new PieChart.Data(bookName, totalProfit));
            }

            // Close resources
            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.fillInStackTrace();
        }

        return pieChartData;
    }
}
