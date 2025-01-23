package application.bookstore.views;

import application.bookstore.auxiliaries.Alerts;
import application.bookstore.models.Book;
import application.bookstore.models.Supplier;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddBookView {
    private GridPane pane;

    private Label supplierPhoneLabel;

    public TextField isbnTextField;
    public TextField titleTextField;
    public TextField authorTextField;
    public TextField categoryTextField;
    public TextField descriptionTextField;
    public TextField originalPriceTextField;
    public TextField sellingPriceTextField;
    public TextField quantityTextField;

    public TextField supplierNameTextField;
    public TextField supplierEmailTextField;
    public TextField supplierPhoneTextField;
    public TextField supplierAddressTextField;
    private ImageView imageView;
    public File selectedImageFile;
    private Book book;
    private static final String ISBN_PATTERN = "\\d{13}";
    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
    private static final String PHONE_PATTERN = "\\d{10}";


    public Scene showView(Stage stage) {
        BorderPane mainPane = new BorderPane();
        pane = createBookEntryForm();
        HBox hbox = new HBox();
        Button addBook = new Button("Add Book");
        addBook.setId("confirm-Book");
        addBook.setMinHeight(50);
        addBook.setMinWidth(150);
        addBook.setOnAction(e -> {createBook(); stage.close();});
        Button cancelButton = new Button("Cancel");
        cancelButton.setMinHeight(50);
        cancelButton.setMinWidth(150);
        cancelButton.setOnAction(e -> stage.close());

        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(30);
        hbox.getChildren().addAll(addBook, cancelButton);

        mainPane.setBottom(hbox);
        mainPane.setCenter(pane);

        mainPane.setStyle("-fx-background-color: #f0f0f0;");
        mainPane.setPadding(new Insets(50));

        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(20));

        addBook.setStyle("-fx-font-size: 18;");
        cancelButton.setStyle("-fx-font-size: 18;");

        stage.setTitle("Add Book");
        return new Scene(mainPane, 800, 800);
    }

    private GridPane createBookEntryForm() {
        pane = new GridPane();
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setVgap(15);
        pane.setHgap(15);

        Label isbnLabel = new Label("ISBN:");
        Label titleLabel = new Label("Title:");
        Label authorLabel = new Label("Author:");
        Label categoryLabel = new Label("Category:");
        Label descriptionLabel = new Label("Description:");
        Label originalPriceLabel = new Label("Original Price:");
        Label sellingPriceLabel = new Label("Selling Price:");
        Label quantityLabel = new Label("Quantity:");

        Label supplierNameLabel = new Label("Supplier Name:");
        Label supplierEmailLabel = new Label("Supplier Email:");
        supplierPhoneLabel = new Label("Supplier Phone:");
        Label supplierAddressLabel = new Label("Supplier Address:");
        Label image_label = new Label("Book Image:");

        isbnLabel.setStyle("-fx-font-size: 16;");
        titleLabel.setStyle("-fx-font-size: 16;");
        authorLabel.setStyle("-fx-font-size: 16;");
        categoryLabel.setStyle("-fx-font-size: 16;");
        descriptionLabel.setStyle("-fx-font-size: 16;");
        originalPriceLabel.setStyle("-fx-font-size: 16;");
        sellingPriceLabel.setStyle("-fx-font-size: 16;");
        quantityLabel.setStyle("-fx-font-size: 16;");
        supplierNameLabel.setStyle("-fx-font-size: 16;");
        supplierEmailLabel.setStyle("-fx-font-size: 16;");
        supplierPhoneLabel.setStyle("-fx-font-size: 16;");
        supplierAddressLabel.setStyle("-fx-font-size: 16;");
        image_label.setStyle("-fx-font-size: 16;");

        isbnTextField = new TextField();
        isbnTextField.setId("isbnTextField");
        titleTextField = new TextField();
        titleTextField.setId("titleTextField");
        authorTextField = new TextField();
        authorTextField.setId("authorTextField");
        categoryTextField = new TextField();
        categoryTextField.setId("categoryTextField");
        descriptionTextField = new TextField();
        descriptionTextField.setId("descriptionTextField");
        originalPriceTextField = new TextField();
        originalPriceTextField.setId("originalPriceTextField");
        sellingPriceTextField = new TextField();
        sellingPriceTextField.setId("sellingPriceTextField");
        quantityTextField = new TextField();
        quantityTextField.setId("quantityTextField");

        supplierNameTextField = new TextField();
        supplierNameTextField.setId("supplierNameTextField");
        supplierEmailTextField = new TextField();
        supplierEmailTextField.setId("supplierEmailTextField");
        supplierPhoneTextField = new TextField();
        supplierPhoneTextField.setId("supplierPhoneTextField");
        supplierAddressTextField = new TextField();
        supplierAddressTextField.setId("supplierAddressTextField");

        imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        
        Button chooseImageButton = new Button("Choose Image");
        chooseImageButton.setId("chooseImageButton");
        chooseImageButton.setMinHeight(50);
        chooseImageButton.setMinWidth(150);
        chooseImageButton.setOnAction(e -> chooseImage());

        //inout validation



        pane.addRow(0, isbnLabel, isbnTextField);
        pane.addRow(1, titleLabel, titleTextField);
        pane.addRow(2, authorLabel, authorTextField);
        pane.addRow(3, categoryLabel, categoryTextField);
        pane.addRow(4, descriptionLabel, descriptionTextField);
        pane.addRow(5, originalPriceLabel, originalPriceTextField);
        pane.addRow(6, sellingPriceLabel, sellingPriceTextField);
        pane.addRow(7, quantityLabel, quantityTextField);
        pane.addRow(8, supplierNameLabel, supplierNameTextField);
        pane.addRow(9, supplierEmailLabel, supplierEmailTextField);
        pane.addRow(10, supplierPhoneLabel, supplierPhoneTextField);
        pane.addRow(11, supplierAddressLabel, supplierAddressTextField);
        pane.addRow(12, image_label, imageView, chooseImageButton);

        return pane;
    }
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Book Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        selectedImageFile = fileChooser.showOpenDialog(null);

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            imageView.setImage(image);
        }
    }
    public void createBook() {
        try {
            if (!validateISBN(isbnTextField.getText())) {
                Alerts.showAlert(Alert.AlertType.ERROR, "Invalid ISBN", "Please enter a valid ISBN");
                return;
            }

            if (!validateEmail(supplierEmailTextField.getText())) {
                Alerts.showAlert(Alert.AlertType.ERROR, "Invalid Supplier Email", "Please enter a valid Supplier Email");
                return;
            }

            if (!validatePhoneNumber(supplierPhoneTextField.getText())) {
                Alerts.showAlert(Alert.AlertType.ERROR, "Invalid Supplier Phone Number", "Please enter a valid Phone Number");
                return;
            }

            Supplier supplier = new Supplier(supplierNameTextField.getText(), supplierEmailTextField.getText(),
                    supplierPhoneLabel.getText(), supplierAddressTextField.getText());

            book = new Book(
                    isbnTextField.getText(),
                    titleTextField.getText(),
                    authorTextField.getText(),
                    categoryTextField.getText(),
                    supplier.findSupplierId(),
                    descriptionTextField.getText(),
                    Double.parseDouble(originalPriceTextField.getText()),
                    Double.parseDouble(sellingPriceTextField.getText()),
                    Integer.parseInt(quantityTextField.getText())
            );

            if (selectedImageFile != null) {
                book.saveImageLocally(selectedImageFile);
            } else {
                book.saveImageLocally(new File("/Users/regiloshi/Downloads/Unknown-5.png"));
            }

            book.saveToDatabase();

        } catch (NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private boolean validateISBN(String isbn) {
        Pattern pattern = Pattern.compile(ISBN_PATTERN);
        Matcher matcher = pattern.matcher(isbn);
        return matcher.matches();
    }

    private boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean validatePhoneNumber(String phone) {
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

}



