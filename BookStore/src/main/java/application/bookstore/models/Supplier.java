package application.bookstore.models;

import application.bookstore.auxiliaries.DatabaseConnector;

import java.sql.*;

public class Supplier implements DatabaseConnector {
    private int supplierId;
    private final boolean exists = false;
    private String name, email, phoneNumber, address;


    public Supplier(int supplierId, String name, String email, String phoneNumber, String address) {
        this.supplierId = supplierId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public Supplier(String name, String email, String phoneNumber, String address) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public static Supplier getSupplierDB(int supplierId) {
        Supplier supplier = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Supplier where SupplierId = " + supplierId);
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phoneNumber = resultSet.getString("phoneNumber");
                String address = resultSet.getString("address");

                supplier = new Supplier(supplierId, name, email, phoneNumber, address);
            }
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return supplier;
    }
    public boolean supplierExists(int supplierId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            String query = "SELECT * FROM Supplier WHERE SupplierId = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, supplierId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    public int saveToDatabase() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String insertSql = "INSERT INTO Supplier (name, email, phoneNumber, address) VALUES (?, ?, ?, ?)";
            String selectSql = "SELECT SupplierId FROM Supplier WHERE email = ?";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertStatement.setString(1, this.name);
                insertStatement.setString(2, this.email);
                insertStatement.setString(3, this.phoneNumber);
                insertStatement.setString(4, this.address);
                insertStatement.executeUpdate();
                try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.supplierId = generatedKeys.getInt(1);
                        return supplierId;
                    } else {
                        throw new SQLException("Failed to retrieve SupplierId.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int findSupplierId() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String selectSql = "SELECT * FROM Supplier WHERE email = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
                selectStatement.setString(1, this.email);
                ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    this.supplierId = resultSet.getInt("SupplierId");
                } else {
                    this.supplierId = this.saveToDatabase();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.supplierId;
    }

    public void setSupplierId(int id) {
        this.supplierId = id;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}