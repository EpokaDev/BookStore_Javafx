package application.bookstore.models;


import application.bookstore.auxiliaries.DatabaseConnector;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class Book  implements DatabaseConnector {
  private int supplierid;
  private final boolean exists = false;
  private String ISBN, title, author, category, description;
  private final SimpleObjectProperty<ImageView> bookImageProperty = new SimpleObjectProperty<>();
  private Date purchaseDate;
  private double purchasedPrice, originalPrice, sellingPrice;
  private int quantity;
  private Supplier supplier;
  private final IntegerProperty chosenQuantity = new SimpleIntegerProperty(0);
  private String imageUrl;

    //Constructor made for Book object which will be added to table , so purchased date and purchased price are not needed
  public Book(String ISBN, String name, String author,
              String category, int supplierid, String description,
              Image image, double originalPrice, double sellingPrice, int quantity) {
    this.ISBN = ISBN;
    this.title = name;
    this.author = author;
    this.category = category;
    this.supplierid = supplierid;
    this.description = description;
    setBookImageProperty(image);
    this.originalPrice = originalPrice;
    this.sellingPrice = sellingPrice;
    this.quantity = quantity;
  }

  public Book(String ISBN, String name, String author, String category, String description,
              Image image, double originalPrice, double sellingPrice, int quantity) {
    this.ISBN = ISBN;
    this.title = name;
    this.author = author;
    this.category = category;
    this.description = description;
    setBookImageProperty(image);
    this.originalPrice = originalPrice;
    this.sellingPrice = sellingPrice;
    this.quantity = quantity;
  }

  //Constructor made for Book object which will be bought
  public Book(String ISBN, String name, String author,
              String category, Supplier supplier, String description,
              Image image, Date purchaseDate, double purchasedPrice,
              double originalPrice, double sellingPrice, int quantity) {
    this.ISBN = ISBN;
    this.title = name;
    this.author = author;
    this.category = category;
    this.supplier = supplier;
    this.description = description;
    setBookImageProperty(image);
    this.purchaseDate = purchaseDate;
    this.purchasedPrice = purchasedPrice;
    this.originalPrice = originalPrice;
    this.sellingPrice = sellingPrice;
    this.quantity = quantity;
  }

  public Book(String ISBN, String title, String author, String cat, String desc, double originalPrice, double sellingPrice, int quantity) {
    this.ISBN = ISBN;
    this.title = title;
    this.author = author;
    this.category = cat;
    this.description = desc;
    this.originalPrice = originalPrice;
    this.sellingPrice = sellingPrice;
    this.quantity = quantity;
  }
  public Book(String ISBN, String title, String author, String cat, int supplierid ,  String desc, double originalPrice, double sellingPrice, int quantity) {
    this.ISBN = ISBN;
    this.title = title;
    this.author = author;
    this.category = cat;
    this.supplierid = supplierid;
    this.description = desc;
    this.originalPrice = originalPrice;
    this.sellingPrice = sellingPrice;
    this.quantity = quantity;
  }

  public String getISBN() {
    return ISBN;
  }

  public void setISBN(String ISBN) {
    this.ISBN = ISBN;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String name) {
    this.title = name;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Supplier getSupplier() {
    return supplier;
  }

  public void setSupplier(Supplier supplier) {
    this.supplier = supplier;
  }

  public void setSupplierId(Supplier supplier) {
    supplierid = supplier.getSupplierId();
    try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
      String updateSql = "UPDATE Book SET supplierId = ? WHERE ISBN = ?";

      try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
        updateStatement.setInt(1, supplier.getSupplierId());
        updateStatement.setString(2, this.ISBN);
        updateStatement.executeUpdate();
      }
    } catch (SQLException e) {
      e.fillInStackTrace();
    }
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ImageView getBookImageProperty() {
    return bookImageProperty.get();
  }

  public void setBookImageProperty(Image image) {
    this.bookImageProperty.set(new ImageView(image));
  }

  public Date getPurchaseDate() {
    return purchaseDate;
  }

  public void setPurchaseDate(Date purchaseDate) {
    this.purchaseDate = purchaseDate;
  }

  public double getPurchasedPrice() {
    return purchasedPrice;
  }

  public void setPurchasedPrice(double purchasedPrice) {
    this.purchasedPrice = purchasedPrice;
  }

  public double getOriginalPrice() {
    return originalPrice;
  }

  public void setOriginalPrice(double originalPrice) {
    this.originalPrice = originalPrice;
  }

  public double getSellingPrice() {
    return sellingPrice;
  }

  public void setSellingPrice(double sellingPrice) {
    this.sellingPrice = sellingPrice;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public void decrementQuantity(int x)
  {
    this.quantity-=x;
  }

  public final IntegerProperty chosenQuantityProperty() {
    return this.chosenQuantity;
  }

  public final int getChosenQuantity() {
    return this.chosenQuantityProperty().get();
  }

  public final void setChosenQuantity(int chosenQuantity) {
    if(chosenQuantity < 0){
      throw new IllegalArgumentException("Chosen quantity cannot be negative");
    }
    this.chosenQuantityProperty().set(chosenQuantity);
  }



  @Override
  public String toString() {
    return "Book{" +
            "ISBN='" + ISBN + '\'' +
            ", category='" + category + '\'' +
            ", name='" + title + '\'' +
            ", author='" + author + '\'' +
            ", category='" + category + '\'' +
            ", supplier='" + supplier + '\'' +
            ", description='" + description + '\'' +
            ", purchaseDate=" + purchaseDate +
            ", purchasedPrice=" + purchasedPrice +
            ", originalPrice=" + originalPrice +
            ", sellingPrice=" + sellingPrice +
            ", quantity=" + quantity +
            '}';
  }
  public void saveImageLocally(File sourceImageFile) {
    try {
      String baseFolderPath = System.getProperty("user.dir");
      String subFolderPath = "BookImages";
      String folderPath = baseFolderPath + File.separator + subFolderPath;
      String destinationPath = folderPath + File.separator + ISBN + ".png";

      Files.createDirectories(Paths.get(baseFolderPath));
      Files.createDirectories(Paths.get(folderPath));
      Files.copy(sourceImageFile.toPath(), new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
      this.imageUrl = "file:" + File.separator + File.separator + destinationPath;
    } catch (IOException e) {
      e.fillInStackTrace();
    }
  }
  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
  public Image getLocalImage() {
    if (imageUrl != null && !imageUrl.isEmpty()) {
      return new Image(imageUrl);
    }
    return null;
  }

  public void saveToDatabase() {
    try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
      String sql = "INSERT INTO Book (ISBN, name, author, category, supplier, description, bookURL, original_price, selling_price, quantity) " +
              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

      try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setString(1, ISBN);
        preparedStatement.setString(2, title);
        preparedStatement.setString(3, author);
        preparedStatement.setString(4, category);
        preparedStatement.setInt(5, supplierid);
        preparedStatement.setString(6, description);
        preparedStatement.setString(7, imageUrl);
        preparedStatement.setDouble(8, originalPrice);
        preparedStatement.setDouble(9, sellingPrice);
        preparedStatement.setInt(10, quantity);
        System.out.println(preparedStatement);
        preparedStatement.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  public void updateInDatabase() {
    try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
      String sql = "UPDATE Book SET name = COALESCE(?, name), author = COALESCE(?, author), category = COALESCE(?, category), " +
              "description = COALESCE(?, description), bookURL = COALESCE(?, bookURL), original_price = COALESCE(?, original_price), " +
              "selling_price = COALESCE(?, selling_price), quantity = COALESCE(?, quantity) WHERE ISBN = ?";

      try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setString(1, title.isEmpty() ? null : title);
        preparedStatement.setString(2, author.isEmpty() ? null : author);
        preparedStatement.setString(3, category.isEmpty() ? null : category);
        preparedStatement.setString(4, description.isEmpty() ? null : description);
        preparedStatement.setString(5, imageUrl == null || imageUrl.isEmpty() ? null : imageUrl);
        preparedStatement.setDouble(6, originalPrice);
        preparedStatement.setDouble(7, sellingPrice);
        preparedStatement.setInt(8, quantity);
        preparedStatement.setString(9, ISBN);

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected == 0) {
          System.out.println("No book with ISBN " + ISBN + " found for update.");
        } else {
          System.out.println("Book with ISBN " + ISBN + " updated successfully.");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}


