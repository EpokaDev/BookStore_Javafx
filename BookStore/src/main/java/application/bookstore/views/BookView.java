package application.bookstore.views;

import application.bookstore.auxiliaries.Alerts;
import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.charts.Chart1;
import application.bookstore.controllers.BookController;
import application.bookstore.controllers.BookList;
import application.bookstore.controllers.FilterController;
import application.bookstore.models.Book;
import application.bookstore.models.User;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.function.Predicate;


public class BookView implements DatabaseConnector {
    BorderPane pane;
    private final ObservableList<Book> selectedBooks = FXCollections.observableArrayList();
    StringProperty role;
    private TextField search_field;
    private Label totalSumLabel;
    private ChoiceBox<Integer> choiceBox;
    public final User user;
    private BookList bookList;
    public ArrayList<Book> books;
    private TableView<Book> tableView;
    private EditBookView editBookView;
    private AddBookView addBookView;

    public BookView(StringProperty role, User user, EditBookView editBookView) {
        this.role = role;
        this.user = user;
        this.editBookView = editBookView;
    }

    public BookView(StringProperty role, User user, AddBookView addBookView) {
        this.role = role;
        this.user = user;
        this.addBookView = addBookView;
    }


    public BookView(StringProperty role ,User user) {
        this.role = role;
        this.user = user;
    }

    public TableView<Book> getTableView() {
        return tableView;
    }

    public ObservableList<Book> getSelectedBooks() {
        return selectedBooks;
    }

    public void generateBill(BookController bookController) {
        if (selectedBooks.isEmpty()) {
            // Display error if no books are selected
            Alerts.showAlert(Alert.AlertType.ERROR, "No Books Selected!", "Please select at least one book to generate a bill.");
            return;
        }

        try {
            // Calculate total amount
            double totalAmount = calculateTotalSum();

            // Generate the bill and save it to the database
            bookController.generateBillToDatabase(selectedBooks, totalAmount, user);

            // Display success message
            Alerts.showAlert(Alert.AlertType.INFORMATION, "Success", "Bill generated successfully!");

            // Clear selected books and refresh UI
            selectedBooks.clear();
            tableView.refresh();
            totalSumLabel.setText("Total Sum: $0.00");
        } catch (Exception e) {
            // Handle any errors during bill generation
            Alerts.showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate bill: " + e.getMessage());
        }
    }

    public void addBookButtonClicked() {
        if (addBookView == null) {
            addBookView = new AddBookView();
        }
        Stage popup = new Stage();
        try {
            popup.setScene(addBookView.showView(popup));
            popup.show();
        } catch (Exception e) {
            Alerts.showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Add Book View: " + e.getMessage());
        }
    }

    public void editBook(Book book) {
        if (editBookView == null) {
            editBookView = new EditBookView(book); // Fallback for production
        }
        Stage popup = new Stage();
        popup.setScene(editBookView.showView(popup));
        popup.show();
    }

    public Scene showView(Stage stage) {
        pane = new BorderPane();

        Label search_label = new Label("Search for a book title: ");
        search_label.setMinHeight(40);

        search_field = new TextField();
        search_field.setPromptText("Enter a book title...");
        search_field.setMinWidth(600);
        search_field.setMinHeight(40);


        tableView = new TableView<>();
        TableView<Book> buying_tableView = new TableView<>();
        tableView.setRowFactory(tv -> {
            TableRow<Book> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Book selectedBook = row.getItem();

                    Stage actionStage = new Stage();
                    HBox actionBox = new HBox();
                    Scene actionScene = new Scene(actionBox, 200, 100);

                    Button editButton = new Button("Edit Book");
                    editButton.setMinWidth(30);
                    editButton.setMinHeight(30);
                    Button deleteButton = new Button("Delete Book");
                    deleteButton.setMinWidth(30);
                    deleteButton.setMinHeight(30);

                    editButton.setOnAction(e -> {
                        EditBookView editBookView = new EditBookView(selectedBook);
                        try {
                            Stage editStage = new Stage();
                            editStage.setScene(editBookView.showView(editStage));
                            editStage.show();
                            bookList = new BookList();
                            books = bookList.getBooks();
                            tableView.getItems().addAll(books);
                            tableView.refresh();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        actionStage.close();
                    });

                    deleteButton.setOnAction(e -> {
                        BookController.deleteBook(selectedBook.getISBN());
                        tableView.getItems().remove(selectedBook);
                        tableView.refresh();
                        actionStage.close();
                    });

                    actionBox.setAlignment(Pos.CENTER);
                    actionBox.setSpacing(10);
                    actionBox.getChildren().addAll(editButton, deleteButton);

                    actionStage.setScene(actionScene);
                    actionStage.show();
                }
            });
            return row;
        });

        TableColumn<Book, Long> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(
                new PropertyValueFactory<>("ISBN"));
        isbnCol.setMinWidth(115);

        TableColumn<Book, Long> buyIsbnCol = new TableColumn<>("ISBN");
        buyIsbnCol.setCellValueFactory(
                new PropertyValueFactory<>("ISBN"));
        buyIsbnCol.setMinWidth(115);


        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(
                new PropertyValueFactory<>("title"));
        titleCol.setMinWidth(115);
        titleCol.setCellFactory(col -> {
            return new TableCell<Book, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        Text text = new Text(item);
                        text.wrappingWidthProperty().bind(titleCol.widthProperty());
                        setGraphic(text);
                        setWrapText(true);
                    }
                }
            };
        });

        TableColumn<Book, String> buyTitleCol = new TableColumn<>("Title");
        buyTitleCol.setCellValueFactory(
                new PropertyValueFactory<>("title"));
        buyTitleCol.setMinWidth(115);
        buyTitleCol.setCellFactory(col -> {
            TableCell<Book, String> cell = new TableCell<Book, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        Text text = new Text(item);
                        text.wrappingWidthProperty().bind(buyTitleCol.widthProperty());
                        setGraphic(text);
                        setWrapText(true);
                    }
                }
            };
            return cell;
        });

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(
                new PropertyValueFactory<>("author"));
        authorCol.setMinWidth(115);

        TableColumn<Book, String> buyAuthorCol = new TableColumn<>("Author");
        buyAuthorCol.setCellValueFactory(
                new PropertyValueFactory<>("author"));
        buyAuthorCol.setMinWidth(115);

        TableColumn<Book, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(
                new PropertyValueFactory<>("category"));
        categoryCol.setMinWidth(115);

        TableColumn<Book, String> sellingPrice_col = new TableColumn<>("Price");
        sellingPrice_col.setCellValueFactory(
                new PropertyValueFactory<>("sellingPrice"));
        sellingPrice_col.setMinWidth(115);


        TableColumn<Book, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(
                new PropertyValueFactory<>("description"));
        descriptionCol.setCellFactory(col -> {
            TableCell<Book, String> cell = new TableCell<Book, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        Text text = new Text(item);
                        text.wrappingWidthProperty().bind(descriptionCol.widthProperty());
                        setGraphic(text);
                        setWrapText(true);
                    }
                }
            };
            return cell;
        });
        descriptionCol.setMinWidth(160);



        TableColumn<Book, ImageView> imageCol = getBookImageViewTableColumn();


        TableColumn<Book, ImageView> buyImageCol = getBookImageViewTableColumn();

        TableColumn<Book, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));
        quantityCol.setMinWidth(115);



        TableColumn<Book ,CheckBox> selectCol = new TableColumn<>("");
        selectCol.setSortable(false);
        selectCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Book, CheckBox>, ObservableValue<CheckBox>>() {
            @Override
            public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<Book, CheckBox> chosenBook) {
                Book chosen_book = chosenBook.getValue();
                CheckBox checkBox = new CheckBox();
                checkBox.selectedProperty().setValue(false);
                checkBox.selectedProperty().addListener((ov, old_val, new_val) -> {
                    if (new_val) {
                        selectedBooks.add(chosen_book);
                    } else {
                        selectedBooks.remove(chosen_book);
                        updateTotalSumLabel();
                    }
                    buying_tableView.refresh();
                });
                return new SimpleObjectProperty<>(checkBox);
            }
        });


        TableColumn<Book ,CheckBox> selectBuyCol = new TableColumn<>("");
        FileInputStream file2;
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if(os.contains("win"))
            {
                file2 = new FileInputStream("C:\\Users\\alvin\\OneDrive\\Desktop\\BookStoreJavafx\\BookStore\\Images\\touch.png");
            }else file2 = new FileInputStream("/Users/regiloshi/IdeaProjects/BookStore_Javafx/BookStore/Images/touch.png");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Image img2 = new Image(file2);
        ImageView image2 = new ImageView(img2);
        image2.setFitWidth(20);
        image2.setFitHeight(20);
        selectBuyCol.setGraphic(image2);
        selectBuyCol.setSortable(false);
        selectBuyCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Book, CheckBox>, ObservableValue<CheckBox>>() {
            @Override
            public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<Book, CheckBox> chosenBook) {
                Book chosen_book = chosenBook.getValue();
                CheckBox checkBox = new CheckBox();
                checkBox.selectedProperty().setValue(true);
                checkBox.selectedProperty().addListener((ov, old_val, new_val) -> {
                    if (!new_val){
                        selectedBooks.remove(chosen_book);
                        choiceBox.setValue(0);
                    }
                });
                return new SimpleObjectProperty<CheckBox>(checkBox);
            }
        });

        TableColumn<Book, Double> totalPriceCol = new TableColumn<>("Total Price");
        totalPriceCol.setCellValueFactory(cellData -> {
            Book book = cellData.getValue();
            double originalPrice = book.getSellingPrice();
            int chosenQuantity = book.getChosenQuantity();
            double totalPrice = originalPrice * chosenQuantity;
            return new SimpleObjectProperty<>(totalPrice);
        });
        totalPriceCol.setMinWidth(115);

        TableColumn<Book, ChoiceBox<Integer>> buyQuantityCol = new TableColumn<>("Choose Quantity");
        buyQuantityCol.setCellValueFactory(cellData -> {
            Book book = cellData.getValue();
            ChoiceBox<Integer> choiceBox = new ChoiceBox<>();
            for (int i = 0; i <= book.getQuantity(); i++) {
                choiceBox.getItems().add(i);
            }
            choiceBox.setValue(book.getChosenQuantity());
            choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                book.setChosenQuantity(newVal);
                updateTotalSumLabel();
            });

            return new SimpleObjectProperty<>(choiceBox);
        });
        buyQuantityCol.setMinWidth(115);



        buying_tableView.getColumns().addAll(selectBuyCol , buyQuantityCol , sellingPrice_col,totalPriceCol, buyIsbnCol , buyTitleCol , buyAuthorCol
        , buyImageCol );

        buying_tableView.setItems(selectedBooks);


        tableView.getColumns().addAll(selectCol , isbnCol , titleCol , authorCol ,
                categoryCol , descriptionCol , imageCol  , quantityCol);

        bookList = new BookList();
        books = bookList.getBooks();
        if(!(user.getRoleString().equalsIgnoreCase("librarian")) && !(books.isEmpty())){
            BookList.notifyLowQuantity();
        }
        tableView.getItems().addAll(books);

        VBox tables = new VBox();
        tables.getChildren().addAll(tableView , buying_tableView);

        ComboBox<String> filterComboBox = FilterController.createFilterComboBox(bookList.getCategories());

        Button search_button = new Button("Search");
        search_button.setMinWidth(30);
        search_button.setMinHeight(30);
        search_button.setOnAction(event -> {
            String searchText = search_field.getText();
            String selectedCategory = filterComboBox.getValue();
            filterTable(selectedCategory, searchText);
        });
        search_button.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                        String searchText = search_field.getText();
                        String selectedCategory = filterComboBox.getValue();
                        filterTable(selectedCategory, searchText);
                    }
                });
        filterComboBox.setOnAction(event -> filterTable(filterComboBox.getValue(), search_field.getText()));
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(search_label, search_field , search_button, filterComboBox);
        hbox.setSpacing(30);

        HBox hbox_bottom = new HBox();

        Button generateBill = new Button("Generate Bill");
        generateBill.setMinWidth(50);
        generateBill.setMinHeight(50);
        generateBill.setOnAction( e->{
            if(selectedBooks.isEmpty()){
                Alerts.showAlert(Alert.AlertType.ERROR , "No Books Selected!" , "Please Select Book!");
            }else {
                BookController.generateBillToDatabase(selectedBooks , calculateTotalSum() , user);
                BookController.generateBill(user, selectedBooks, calculateTotalSum());
                for (Book selectedBook : selectedBooks) {
                    int newQuantity = selectedBook.getQuantity() - selectedBook.getChosenQuantity();
                    selectedBook.setQuantity(newQuantity);
                }
                selectedBooks.clear();
                tableView.refresh();
                totalSumLabel.setText("");
                buying_tableView.refresh();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Successfully Generated Bill");
                alert.show();
            }
        });
        totalSumLabel = new Label("Total amount ");
        totalSumLabel.setStyle("-fx-font-size: 20px;");

        Button clearAllButton = new Button("Clear");
        clearAllButton.setMinWidth(50);
        clearAllButton.setMinHeight(50);
        clearAllButton.setOnAction(event -> {
            selectedBooks.clear();
            tableView.refresh();
            buying_tableView.refresh();
            updateTotalSumLabel();
        });

        Button goBackButton = new Button("Go Back");
        goBackButton.setId("goBack");
        goBackButton.setMinWidth(50);
        goBackButton.setMinHeight(50);
        goBackButton.setOnAction(event -> {
            AdminView adminView = new AdminView(user);
            try {
                stage.setScene(adminView.showView(stage));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        if(!(user.getRoleString().equalsIgnoreCase("librarian"))){
            Button addBook = getButton();
            if(user.getRoleString().equalsIgnoreCase("admin")) {
                hbox_bottom.getChildren().addAll(totalSumLabel, generateBill, clearAllButton, addBook , goBackButton);
            }else{
                Button statistic = new Button("Statistic");
                statistic.setMinWidth(50);
                statistic.setMinHeight(50);
                statistic.setOnAction(event -> {
                            Chart1 chart1 = new Chart1(user);
                            stage.setScene(chart1.showView(stage));
                        });
                hbox_bottom.getChildren().addAll(totalSumLabel, generateBill, clearAllButton, addBook , statistic );
            }
        }else{
            hbox_bottom.getChildren().addAll(totalSumLabel , generateBill , clearAllButton );
        }
        hbox_bottom.setAlignment(Pos.CENTER);
        hbox_bottom.setSpacing(30);
        hbox_bottom.setMinWidth(150);
        hbox_bottom.setMinHeight(150);

        pane.setCenter(tables);
        pane.setFocusTraversable(true);
        pane.setTop(hbox);
        pane.setBottom(hbox_bottom);
        stage.setTitle("Books");
        return new Scene(pane, 1000 , 700 );
    }

    private static Button getButton() {
        Button addBook = new Button("Add Book");
        addBook.setId("addBookButton");
        addBook.setOnAction(event -> {
            Stage popup = new Stage();
            AddBookView addBookView = new AddBookView();
            try {
                popup.setScene(addBookView.showView(popup));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            popup.show();
        });
        addBook.setMinWidth(50);
        addBook.setMinHeight(50);
        return addBook;
    }

    public void filterTable(String selectedCategory, String searchText) {
        Predicate<Book> predicate = book -> {
            boolean categoryMatch = "All".equals(selectedCategory) || book.getCategory().equalsIgnoreCase(selectedCategory);
            boolean titleMatch = searchText.isEmpty() || book.getTitle().toLowerCase().contains(searchText.toLowerCase());
            return categoryMatch && titleMatch;
        };
        FilteredList<Book> filteredList = new FilteredList<>(FXCollections.observableArrayList(books));
        filteredList.setPredicate(predicate);
        tableView.setItems(filteredList);
        tableView.refresh();
    }



    private static TableColumn<Book, ImageView> getBookImageViewTableColumn() {
        TableColumn<Book, ImageView> imageCol = new TableColumn<>("Image");
        imageCol.setCellValueFactory(new PropertyValueFactory<>("bookImageProperty"));
        imageCol.setCellFactory(col -> new TableCell<Book, ImageView>() {
            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    ImageView imageView = new ImageView(item.getImage());
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    setGraphic(imageView);
                }
            }
        });
        imageCol.setMinWidth(160);
        return imageCol;
    }
    public double calculateTotalSum() {
        double totalSum = 0.0;
        for (Book book : selectedBooks) {
            double originalPrice = book.getSellingPrice();
            int chosenQuantity = book.getChosenQuantity();
            totalSum += originalPrice * chosenQuantity;
        }
        return totalSum;
    }
    private void updateTotalSumLabel() {
        double totalSum = calculateTotalSum();
        totalSumLabel.setText("Total Sum: " + String.format("%.2f", totalSum));
    }

}
